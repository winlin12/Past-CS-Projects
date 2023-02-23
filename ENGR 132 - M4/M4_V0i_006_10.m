function[V0i] = M4_V0i_006_10(t, data)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ENGR 132 
% Program Description 
% Calculates V0 for each substrate concentration in the data given, then
% plots it to find a model. This function returns the values of V0i to be
% used in other functions.
%
% Function Call
% M4_V0i_006_10(t, data, t_all, data_all) // Not used on its own, used in main
% 
% Input Arguments
% S - Matrix of Substrate Concentrations for each enzyme
% t - Time matrix of 20 values
% data - 20x10 matrix of the data of each enzyme
% data_all - ?x1 matrix of the origional data
% t_all - ?x1 matrix of all time points for the data
%
% Output Arguments
% V0i - The matrix of V0 values for each substrate concentration
%
% Assignment Information
%   Assignment:     M03, Problem 1
%   Team member:    Winston Lin, wylin@purdue.edu Taylor Duchinski, tduchins@purdue.edu
%   Team ID:        006-10
%   Academic Integrity:
%     [] We worked with one or more peers but our collaboration
%        maintained academic integrity.
%     Peers we worked with: none
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% ____________________
%% INITIALIZATION
% No initilaization, all values needed are passed into the function.
%% ____________________
%% CALCULATIONS
% Calculates V0i based off of enzyme test data passed through.
V0i = mean(data) / mean(t);
% V0i = (mean(t) .* mean(data) - mean(t .* data)) / (mean(t) ^ 2 - mean(t .^ 2));
    
%% ____________________
%% FORMATTED TEXT/FIGURE DISPLAYS

% % tests for each enzyme.
% for count = 1:1:10
%     subplot(2,5,count);
%     y = V0i(count) * t_all;
%     plot(t_all,data_all(:,count));
%     hold on;
%     plot(t_all,y);
%     title("PGOX50 " + concentration(count)+ "[P] (uM)");
%     xlabel('time (s)');
%     ylabel('concentration [S]');
%     ylim([0 (max(data_all(:,count)) + (max(data_all(:,count))  * .10))]);
%     % grid no
% end

%% ____________________
%% RESULTS
% Returns a vector of V0i values for test given

%% ____________________
%% ACADEMIC INTEGRITY STATEMENT
% We have not used source code obtained from any other unauthorized
% source, either modified or unmodified. Neither have we provided
% access to my code to another. The program we are submitting
% is our own original work.



