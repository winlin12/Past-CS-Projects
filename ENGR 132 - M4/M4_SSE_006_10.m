function[] = M3_SSE_006_10(S, V0i, VMax, Km)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ENGR 132 
% Program Description 
% Calculates V0 for each substrate concentration in the data given, then
% plots it to find a model. This function returns the values of V0i to be
% used in other functions.
%
% Function Call
% M2_Algorithm_V0i_006_10(S, t, data) // Not used on its own, used in main
% 
% Input Arguments
% S - Matrix of Substrate Concentrations for each enzyme
% t - Time matrix of 20 values
% data - 20x10 matrix of the data of each enzyme
%
% Output Arguments
% V0i - The matrix of V0 values for each substrate concentration
%
% Assignment Information
%   Assignment:     M02, Problem 1
%   Team member:    Creigh Dircksen, cdirckse@purdue.edu 
%                   Winston Lin, wylin@purdue.edu
%                   Joe Pahoresky, jpahores@purdue.edu
%                   Taylor Duchinski, tduchins@purdue.edu
%   Team ID:        006-10
%   Academic Integrity:
%     [] We worked with one or more peers but our collaboration
%        maintained academic integrity.
%     Peers we worked with: none
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% ____________________
%% INITIALIZATION
V0i_actual = [0.025 0.049 0.099 0.176 0.329 0.563 0.874 1.192 1.361 1.603];
Vmax_actual = 1.806;
Km_actual = 269.74;

%% ____________________
%% CALCULATIONS
% Formula for V0s
V0i_model_actual = (Vmax_actual .* S) ./ (Km_actual + S); % With
V0i_model = (VMax .* S) ./ (Km + S);

% Calculates SSE
SSE_good_VMax_Km = sum((V0i - V0i_model_actual) .^ 2);
SSE_good_V0i = sum((V0i_actual - V0i_model) .^ 2);
SSE_actual = sum((V0i_actual - V0i_model_actual) .^ 2);

% Calculates percent error
error_V0i = abs((V0i - V0i_actual) ./ V0i_actual .* 100);
error_VMax = abs((VMax - Vmax_actual) / Vmax_actual * 100);
error_Km = abs((Km - Km_actual) / Km_actual * 100);

%% ____________________
%% FORMATTED TEXT/FIGURE DISPLAYS
% disps all calculated values
figure();
hold on
plot(S, V0i_model, "-")
plot(S, V0i, "o")
fprintf("The SSE with the given Vmax and Km values is %.2f\n", SSE_good_VMax_Km);
fprintf("The SSE with the given V0i values is %.2f\n", SSE_good_V0i);
fprintf("The error with the given Vmax and Km values is %.2f\n", error_V0i);
fprintf("The error with the given V0i values is %.2f\n", error_VMax);
%% ____________________
%% RESULTS

%% ____________________
%% ACADEMIC INTEGRITY STATEMENT
% We have not used source code obtained from any other unauthorized
% source, either modified or unmodified. Neither have we provided
% access to my code to another. The program we are submitting
% is our own original work.



