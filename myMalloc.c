#include <errno.h>
#include <pthread.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "myMalloc.h"
#include "printing.h"

/* Due to the way assert() prints error messges we use out own assert function
 * for deteminism when testing assertions
 */
#ifdef TEST_ASSERT
  inline static void assert(int e) {
    if (!e) {
      const char * msg = "Assertion Failed!\n";
      write(2, msg, strlen(msg));
      exit(1);
    }
  }
#else
  #include <assert.h>
#endif

/*
 * Mutex to ensure thread safety for the freelist
 */
static pthread_mutex_t mutex;

/*
 * Array of sentinel nodes for the freelists
 */
header freelistSentinels[N_LISTS];

/*
 * Pointer to the second fencepost in the most recently allocated chunk from
 * the OS. Used for coalescing chunks
 */
header * lastFencePost;

/*
 * Pointer to maintian the base of the heap to allow printing based on the
 * distance from the base of the heap
 */ 
void * base;

/*
 * List of chunks allocated by  the OS for printing boundary tags
 */
header * osChunkList [MAX_OS_CHUNKS];
size_t numOsChunks = 0;

/*
 * direct the compiler to run the init function before running main
 * this allows initialization of required globals
 */
static void init (void) __attribute__ ((constructor));

// Helper functions for manipulating pointers to headers
static inline header * get_header_from_offset(void * ptr, ptrdiff_t off);
static inline header * get_left_header(header * h);
static inline header * ptr_to_header(void * p);

// Helper functions for allocating more memory from the OS
static inline void initialize_fencepost(header * fp, size_t left_size);
static inline void insert_os_chunk(header * hdr);
static inline void insert_fenceposts(void * raw_mem, size_t size);
static header * allocate_chunk(size_t size);

// Helper functions for freeing a block
static inline void deallocate_object(void * p);

// Helper functions for allocating a block
static inline header * allocate_object(size_t raw_size);

// Helper functions for verifying that the data structures are structurally 
// valid
static inline header * detect_cycles();
static inline header * verify_pointers();
static inline bool verify_freelist();
static inline header * verify_chunk(header * chunk);
static inline bool verify_tags();

static void init();

static bool isMallocInitialized;

/**
 * @brief Helper function to retrieve a header pointer from a pointer and an 
 *        offset
 *
 * @param ptr base pointer
 * @param off number of bytes from base pointer where header is located
 *
 * @return a pointer to a header offset bytes from pointer
 */
static inline header * get_header_from_offset(void * ptr, ptrdiff_t off) {
	return (header *)((char *) ptr + off);
}

/**
 * @brief Helper function to get the header to the right of a given header
 *
 * @param h original header
 *
 * @return header to the right of h
 */
header * get_right_header(header * h) {
	return get_header_from_offset(h, get_size(h));
}

/**
 * @brief Helper function to get the header to the left of a given header
 *
 * @param h original header
 *
 * @return header to the right of h
 */
inline static header * get_left_header(header * h) {
  return get_header_from_offset(h, -h->left_size);
}

/**
 * @brief Fenceposts are marked as always allocated and may need to have
 * a left object size to ensure coalescing happens properly
 *
 * @param fp a pointer to the header being used as a fencepost
 * @param left_size the size of the object to the left of the fencepost
 */
inline static void initialize_fencepost(header * fp, size_t left_size) {
	set_state(fp,FENCEPOST);
	set_size(fp, ALLOC_HEADER_SIZE);
	fp->left_size = left_size;
}

/**
 * @brief Helper function to maintain list of chunks from the OS for debugging
 *
 * @param hdr the first fencepost in the chunk allocated by the OS
 */
inline static void insert_os_chunk(header * hdr) {
  if (numOsChunks < MAX_OS_CHUNKS) {
    osChunkList[numOsChunks++] = hdr;
  }
}

/**
 * @brief given a chunk of memory insert fenceposts at the left and 
 * right boundaries of the block to prevent coalescing outside of the
 * block
 *
 * @param raw_mem a void pointer to the memory chunk to initialize
 * @param size the size of the allocated chunk
 */
inline static void insert_fenceposts(void * raw_mem, size_t size) {
  // Convert to char * before performing operations
  char * mem = (char *) raw_mem;

  // Insert a fencepost at the left edge of the block
  header * leftFencePost = (header *) mem;
  initialize_fencepost(leftFencePost, ALLOC_HEADER_SIZE);

  // Insert a fencepost at the right edge of the block
  header * rightFencePost = get_header_from_offset(mem, size - ALLOC_HEADER_SIZE);
  initialize_fencepost(rightFencePost, size - 2 * ALLOC_HEADER_SIZE);
}

/**
 * @brief Allocate another chunk from the OS and prepare to insert it
 * into the free list
 *
 * @param size The size to allocate from the OS
 *
 * @return A pointer to the allocable block in the chunk (just after the 
 * first fencpost)
 */
static header * allocate_chunk(size_t size) {
  void * mem = sbrk(size);
  
  insert_fenceposts(mem, size);
  header * hdr = (header *) ((char *)mem + ALLOC_HEADER_SIZE);
  set_state(hdr, UNALLOCATED);
  set_size(hdr, size - 2 * ALLOC_HEADER_SIZE);
  hdr->left_size = ALLOC_HEADER_SIZE;
  return hdr;
}

/**
 * @brief Helper allocate an object given a raw request size from the user
 *
 * @param raw_size number of bytes the user needs
 *
 * @return A block satisfying the user's request
 */
static inline header * allocate_object(size_t raw_size) {
  if (raw_size == 0) {
    return NULL;
  }
  // TODO implement allocation
  size_t size = ((raw_size + 7) / 8) * 8; // thanks gustavo
   // sets size to 16 to account for next/prev
  if (size < 16) {
    size = 16;
  }
  // finds freeList possible for size allocated
  int iList = (size / 8) - 1;
  if (iList > N_LISTS - 1) {
    iList = N_LISTS - 1;
  }
  size_t whole_size = size + ALLOC_HEADER_SIZE;
  header *freelist = &freelistSentinels[iList];
  for(; iList < N_LISTS; iList++) {
    freelist = &freelistSentinels[iList];
    if(freelist->next != freelist) {
      break;
    }
  }
  // header to help track freelist that is available
  header * obj = freelist->next;
  header * temp = obj->next;
  int objSize = (int) get_size(obj) - (int) whole_size;
  while (temp != freelist && objSize < 0) {
    if (get_size(temp) > get_size(obj)) {
      obj = temp;
      objSize = (int) get_size(obj) - (int) whole_size;
    }
    temp = temp->next;
  }
  header * newChunk;
  header * obj2 = (header *)((char *) obj + objSize);
  // if there are less than 32 bytes remaining, then return entire freelist
  if (objSize < 32 && objSize >= 0) {
    obj->next->prev = obj->prev;
    obj->prev->next = obj->next;
    set_size_and_state(obj, get_size(obj), 1);
    return (header *) obj->data;
  } else if (objSize < 0) { // if there isn't enough space for the size, then allocate a new chunk
    int size_needed = objSize * (-1);
    int chunks = (size_needed / ARENA_SIZE) + 1; 
    newChunk = allocate_chunk(ARENA_SIZE * chunks);
    header * otherFencePost = get_left_header(get_left_header(newChunk));
    if (get_state(otherFencePost) == 2) { // remove fenceposts and coalesce if they are side-by-side
      set_state(get_right_header(otherFencePost), 0);
      set_size_and_state(otherFencePost, 2 * ALLOC_HEADER_SIZE + get_size(newChunk), 0);
      header * right_block = get_left_header(otherFencePost);
      if (get_state(right_block) == 0) { // check if rightmost block is allocated, if not coalease
        right_block->next->prev = right_block->prev;
        right_block->prev->next = right_block->next;
        set_size(right_block, get_size(right_block) + get_size(otherFencePost));
        otherFencePost = right_block;
      }
      newChunk = otherFencePost;
    } else { // insert chunk otherwise
      insert_os_chunk(get_left_header(newChunk));
    } 
    int newSize = get_size(newChunk) - whole_size; // size of chunk after allocation
    //finds freelist
    int inewSize = ((newSize - ALLOC_HEADER_SIZE) / 8) - 1; 
    if (inewSize > N_LISTS - 1) {
      inewSize = N_LISTS - 1;
    }
    obj2 = (header *)((char *) newChunk + newSize);
    set_size(newChunk, newSize);
    set_size_and_state(obj2, whole_size, 1);
    header * freehead = &freelistSentinels[inewSize];
    newChunk->next = freehead->next;
    newChunk->prev = freehead;
    freehead->next = newChunk;
    newChunk->next->prev = newChunk;
    //set sizes and return
    newChunk->left_size = get_size(get_left_header(newChunk));
    obj2->left_size = get_size(newChunk);
    header * right = get_right_header(obj2);
    right->left_size = get_size(obj2);
    return (header *) obj2->data;
  } else {
    // fix sizes
    set_size(obj, objSize);
    set_size_and_state(obj2, whole_size, 1);
    //reallocate freelist
    int iListObj = ((objSize - ALLOC_HEADER_SIZE) / 8) - 1;
    if (iListObj > N_LISTS - 1) {
      iListObj = N_LISTS - 1;
    }
    if (iListObj != iList) {
      obj->prev->next = obj->next;
      obj->next->prev = obj->prev;
      header * freehead = &freelistSentinels[iListObj];
      obj->next = freehead->next;
      obj->prev = freehead;
      freehead->next = obj;
      obj->next->prev = obj;
    }
    // fix sizes
    obj->left_size = get_size(get_left_header(obj));
    obj2->left_size = get_size(obj);
    header * right = get_right_header(obj2);
    right->left_size = get_size(obj2);
    return (header *) obj2->data;
  }
  
  
  // just in case, but code should never reach here
  (void) raw_size;
  assert(false);
  exit(1);
}

/**
 * @brief Helper to get the header from a pointer allocated with malloc
 *
 * @param p pointer to the data region of the block
 *
 * @return A pointer to the header of the block
 */
static inline header * ptr_to_header(void * p) {
  return (header *)((char *) p - ALLOC_HEADER_SIZE); //sizeof(header));
}

/**
 * @brief Helper to manage deallocation of a pointer returned by the user
 *
 * @param p The pointer returned to the user by a call to malloc
 */
static inline void deallocate_object(void * p) {
  // TODO implement deallocation
  if (p == NULL) { // return if p is null
    return;
  }
  //get current, left, and right header
  header * hdr = ptr_to_header(p);
  header * left = get_left_header(hdr);
  header * right = get_right_header(hdr);
  int size = get_size(hdr) - ALLOC_HEADER_SIZE;
  if (size <= 0) { // double free if the header was already freed, or the header isn't there
    printf("Double Free Detected\n");
    (void) p;
    assert(false);
    exit(1);
  }
  set_state(hdr, 0);
  // preparing to insert chunk to freelist lists
  int iList2 = -1;
  int iList = (size / 8) - 1;
  if (iList > N_LISTS - 1) {
    iList = N_LISTS - 1;
  }
  if (get_state(right) == 0) { // coaleases with right chunk if it is free
    size += get_size(right);
    iList2 = ((get_size(right) - ALLOC_HEADER_SIZE) / 8) - 1;
    if (iList2 > N_LISTS - 1) {
      iList2 = N_LISTS - 1;
    }
    header * freehead = &freelistSentinels[iList2];
    header * current = freehead->next;
    while(current != right) {
      current = current->next;
      if (current == freehead) { // should never happen, only if I messed up placing lists in the freelist
        printf("you did something wrong\n");
        (void) p;
        assert(false);
        exit(1);
      }
    }
    // removes old list and makes a new bigger header
    current->prev->next = current->next;
    current->next->prev = current->prev;
    right = get_right_header(right);
    set_size(get_left_header(right), 0);
    set_size(hdr, size + ALLOC_HEADER_SIZE);
  }
  if (get_state(left) == 0) { // coaleases with left chunk if it is unallocated
    // initialize size
    size += get_size(left);
    iList2 = ((get_size(left) - ALLOC_HEADER_SIZE) / 8) - 1;
    if (iList2 > N_LISTS - 1) {
      iList2 = N_LISTS - 1;
    }
    iList = (size / 8) - 1;
    if (iList > N_LISTS - 1) {
        iList = N_LISTS - 1;
    }
    // find freelist and replacesit, only does it if the list is different
    if (iList2 != iList) {
      header * freehead = &freelistSentinels[iList2];
      header * current = freehead->next;
      while(current != left) {
        current = current->next;
        if (current == freehead) { // should never happen, only if I messed up placing lists in the freelist
          printf("you did something wrong\n");
          (void) p;
          assert(false);
          exit(1);
        }
      }

      current->prev->next = current->next;
      current->next->prev = current->prev;
    }
    //new header is combined left and header
    set_size(hdr, 0);
    set_size(left, size + ALLOC_HEADER_SIZE);
    hdr = left;
  }
  // fixes list if the list is no longer the same 
  iList = (size / 8) - 1;
  if (iList > N_LISTS - 1) {
    iList = N_LISTS - 1;
  }
  if (iList2 != iList) {
    header * freehead = &freelistSentinels[iList];
    hdr->next = freehead->next;
    hdr->prev = freehead;
    freehead->next = hdr;
    hdr->next->prev = hdr;
  }
  right->left_size = get_size(hdr);
  return;
  //just in case, but should never reach here
  (void) p;
  assert(false);
  exit(1);
}

/**
 * @brief Helper to detect cycles in the free list
 * https://en.wikipedia.org/wiki/Cycle_detection#Floyd's_Tortoise_and_Hare
 *
 * @return One of the nodes in the cycle or NULL if no cycle is present
 */
static inline header * detect_cycles() {
  for (int i = 0; i < N_LISTS; i++) {
    header * freelist = &freelistSentinels[i];
    for (header * slow = freelist->next, * fast = freelist->next->next; 
         fast != freelist; 
         slow = slow->next, fast = fast->next->next) {
      if (slow == fast) {
        return slow;
      }
    }
  }
  return NULL;
}

/**
 * @brief Helper to verify that there are no unlinked previous or next pointers
 *        in the free list
 *
 * @return A node whose previous and next pointers are incorrect or NULL if no
 *         such node exists
 */
static inline header * verify_pointers() {
  for (int i = 0; i < N_LISTS; i++) {
    header * freelist = &freelistSentinels[i];
    for (header * cur = freelist->next; cur != freelist; cur = cur->next) {
      if (cur->next->prev != cur || cur->prev->next != cur) {
        return cur;
      }
    }
  }
  return NULL;
}

/**
 * @brief Verify the structure of the free list is correct by checkin for 
 *        cycles and misdirected pointers
 *
 * @return true if the list is valid
 */
static inline bool verify_freelist() {
  header * cycle = detect_cycles();
  if (cycle != NULL) {
    fprintf(stderr, "Cycle Detected\n");
    print_sublist(print_object, cycle->next, cycle);
    return false;
  }

  header * invalid = verify_pointers();
  if (invalid != NULL) {
    fprintf(stderr, "Invalid pointers\n");
    print_object(invalid);
    return false;
  }

  return true;
}

/**
 * @brief Helper to verify that the sizes in a chunk from the OS are correct
 *        and that allocated node's canary values are correct
 *
 * @param chunk AREA_SIZE chunk allocated from the OS
 *
 * @return a pointer to an invalid header or NULL if all header's are valid
 */
static inline header * verify_chunk(header * chunk) {
	if (get_state(chunk) != FENCEPOST) {
		fprintf(stderr, "Invalid fencepost\n");
		print_object(chunk);
		return chunk;
	}
	
	for (; get_state(chunk) != FENCEPOST; chunk = get_right_header(chunk)) {
		if (get_size(chunk)  != get_right_header(chunk)->left_size) {
			fprintf(stderr, "Invalid sizes\n");
			print_object(chunk);
			return chunk;
		}
	}
	
	return NULL;
}

/**
 * @brief For each chunk allocated by the OS verify that the boundary tags
 *        are consistent
 *
 * @return true if the boundary tags are valid
 */
static inline bool verify_tags() {
  for (size_t i = 0; i < numOsChunks; i++) {
    header * invalid = verify_chunk(osChunkList[i]);
    if (invalid != NULL) {
      return invalid;
    }
  }

  return NULL;
}

/**
 * @brief Initialize mutex lock and prepare an initial chunk of memory for allocation
 */
static void init() {
  // Initialize mutex for thread safety
  pthread_mutex_init(&mutex, NULL);

#ifdef DEBUG
  // Manually set printf buffer so it won't call malloc when debugging the allocator
  setvbuf(stdout, NULL, _IONBF, 0);
#endif // DEBUG

  // Allocate the first chunk from the OS
  header * block = allocate_chunk(ARENA_SIZE);

  header * prevFencePost = get_header_from_offset(block, -ALLOC_HEADER_SIZE);
  insert_os_chunk(prevFencePost);

  lastFencePost = get_header_from_offset(block, get_size(block));

  // Set the base pointer to the beginning of the first fencepost in the first
  // chunk from the OS
  base = ((char *) block) - ALLOC_HEADER_SIZE; //sizeof(header);

  // Initialize freelist sentinels
  for (int i = 0; i < N_LISTS; i++) {
    header * freelist = &freelistSentinels[i];
    freelist->next = freelist;
    freelist->prev = freelist;
  }

  // Insert first chunk into the free list
  header * freelist = &freelistSentinels[N_LISTS - 1];
  freelist->next = block;
  freelist->prev = block;
  block->next = freelist;
  block->prev = freelist;
}

/* 
 * External interface
 */
void * my_malloc(size_t size) {
  pthread_mutex_lock(&mutex);
  header * hdr = allocate_object(size); 
  pthread_mutex_unlock(&mutex);
  return hdr;
}

void * my_calloc(size_t nmemb, size_t size) {
  return memset(my_malloc(size * nmemb), 0, size * nmemb);
}

void * my_realloc(void * ptr, size_t size) {
  void * mem = my_malloc(size);
  memcpy(mem, ptr, size);
  my_free(ptr);
  return mem; 
}

void my_free(void * p) {
  pthread_mutex_lock(&mutex);
  deallocate_object(p);
  pthread_mutex_unlock(&mutex);
}

bool verify() {
  return verify_freelist() && verify_tags();
}
