%function[] = M2_Main_006_10()
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ENGR 132 
% Program Description 
% This function reads in the data from the given CSV and manipulates it 
% appropriately to be used in calling other UDFs that calculate
% Km,Vmax and Vi for each test.
%
% Function Call
% M3_Main_006_10();
%
% Input Arguments
% NA
%
% Output Arguments
% NA
%
% Assignment Information
%   Assignment:     M02, Problem 1
%   Team member:    Taylor Duchinski, tduchins@purdue.edu
%                   Creigh Dircksen, cdirckse@purdue.edu 
%                   Winston Lin, wylin@purdue.edu
%                   Joe Pahoresky, jpahores@purdue.edu
%     
%   Team ID:        006-10
%   Academic Integrity:
%     [] We worked with one or more peers but our collaboration
%        maintained academic integrity.
%     Peers we worked with: Name, login@purdue [repeat for each]
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% ____________________
%% INITIALIZATION

%read in raw given data 
Data = readmatrix('Data_PGOX50_enzyme.csv');
Enzyme_data = readmatrix("Data_nextGen_KEtesting_allresults.csv");

%Loading in data sets in easy to manage varables 
time_full = Data(7:end,1); %read in time 
time_short = Data(7:26,1);
concentration = Data(5,2:11);   % read in the uM 

% read in each test set 
Enzyme_PGOX50_All = Data(7:end,2:11);
Enzyme_PGOX50_VI = Data(7:26,2:11);

% clear Data; % Save memory by clearing Data we arent using anymore
V0i_sample = zeros(numel(time_full), 10);

%Given V0i data (micromol/sec)
V0i_actual = [0.025 0.049 0.099 0.176 0.329 0.563 0.874 1.192 1.361 1.603];

%Given vmax (micromol/sec)
Vmax_actual = 1.806;

%Given Km
Km_actual = 269.74;

% temp
close all;
percent_error_low = 100;
%% ____________________
%% CALCULATIONS

% Calculate V0i Average for the reference data set
% Will test each possible combination of points to see what produces the
% best V0i. 
V0i_model_actual = (Vmax_actual .* concentration) ./ (Km_actual + concentration);
for i = 2:1:(numel(time_full))
    Enzyme_PGOX50_VI = Enzyme_PGOX50_All(1:i, :);
    time_short = time_full(1:i, :);
    V0i_sample(i, :) =  M4_V0i_006_10(time_short(),Enzyme_PGOX50_VI());

    % Analyzes parameters gathered from our original functions to reference
    % data given in the document.
    percent_error_average = mean(abs((V0i_sample(i, :) - V0i_actual) ./ V0i_actual));
    if (percent_error_average < percent_error_low)
        percent_error_low = percent_error_average;
        V0i = V0i_sample(i, :);
        maxIndex = i;
    end
end
% Call Vmax and Km UDFs with the appropriate arguments
Vmax = M4_Vmax_006_10(V0i(),concentration(),'PGOX50-A');
Km = M4_Km_006_10(V0i(),concentration(),'PGOX50-A');

% Analyzes parameters gathered from our original functions to reference
% data given in the document.
M4_MM_PGOX50_006_10(concentration, V0i());
% Uses the new method of getting V0i to fetch new parameters for other
% enzyme results.
Enzyme_conc = Enzyme_data(5:maxIndex, 2:end);
Enzyme_t = Enzyme_data(5:maxIndex, 1);
Enzyme_names = ["NextGen-A", "NextGen-B", "NextGen-C", "NextGen-D", "NextGen-E"];
index = 1;
for enzymes = 1:numel(Enzyme_names)
    for tests = [1, 2]
        tempIndex = index + 9;
        V0i_test(tests, 1:10) = M4_V0i_006_10(Enzyme_t, Enzyme_conc(:, index:tempIndex));
        index = tempIndex + 1;
    end
    Enzyme_V0i(enzymes, 1:10) = (V0i_test(1, 1:10) + V0i_test(2, 1:10)) ./ 2;
    Enzyme_VMax(enzymes) = M4_Vmax_006_10(Enzyme_V0i(enzymes, 1:10), concentration, Enzyme_names(enzymes));
    Enzyme_Km(enzymes) = M4_Km_006_10(Enzyme_V0i(enzymes, 1:10), concentration, Enzyme_names(enzymes));
end
%% ____________________
%% FORMATTED TEXT/FIGURE DISPLAYS

% All figures are in UDFs
fprintf("Number of data points used for calculating V0i: %d\n", maxIndex);
%% ____________________
%% RESULTS

% The Vmax for Enzyme Actual V0i Data is 1.700
% Actual V0i Data Km value is 251.20 
% The SSE with the given Vmax and Km values is 0.009
% The SSE with the given V0i values is 0.013
% The SSE with all given values is 0.005.
% The error with the given Vmax and Km values for V01 is 21.263 percent
% The error with the given Vmax and Km values for V02 is 25.832 percent
% The error with the given Vmax and Km values for V03 is 16.332 percent
% The error with the given Vmax and Km values for V04 is 18.529 percent
% The error with the given Vmax and Km values for V05 is 19.456 percent
% The error with the given Vmax and Km values for V06 is 9.530 percent
% The error with the given Vmax and Km values for V07 is 1.497 percent
% The error with the given Vmax and Km values for V08 is 1.956 percent
% The error with the given Vmax and Km values for V09 is 1.388 percent
% The error with the given Vmax and Km values for V010 is 0.274 percent
% The error with VMax is 5.890
% The error with VMax is 6.872
% The error with Km is 5.890
% The error with Km is 6.872

%% ____________________
%% ACADEMIC INTEGRITY STATEMENT
% We have not used source code obtained from any other unauthorized
% source, either modified or unmodified. Neither have we provided
% access to my code to another. The program we are submitting
% is our own original work.



