    function[SSE_good_VMax_Km] = M4_MM_PGOX50_006_10(concentration, V0i)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ENGR 132 
% Program Description 
% Calculates V0 for each substrate concentration in the data given, then
% plots it to find a model. This function returns the values of V0i to be
% used in other functions.
%
% Function Call
% M2_Algorithm_V0i_006_10(S_concentration, t) // Not used on its own, used in main
% 
% Input Arguments
% S_concentration - Matrix of Substrate Concentrations for each enzyme
% t - Time matrix of 20 values
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
%Given V0i data (micromol/sec)
V0i_actual = [0.025 0.049 0.099 0.176 0.329 0.563 0.874 1.192 1.361 1.603];
%Given vmax (micromol/sec)
Vmax_actual = 1.806;
%Given Km
Km_actual = 269.74;
%Calculated Vmax from actual V0i (micromol/sec)
Vmax_good = M4_Vmax_006_10(V0i_actual,concentration,'Actual V0i Data');
%Calculated Km from Actual V0i
Km_good = M4_Km_006_10(V0i_actual,concentration,'Actual V0i Data');

%% ____________________
%% CALCULATIONS
% Formula for V0s
V0i_model_actual = (Vmax_actual .* concentration) ./ (Km_actual + concentration); % With
V0i_model = (Vmax_good .* concentration) ./ (Km_good + concentration);


% Calculates SSE
SSE_good_VMax_Km = sum((V0i - V0i_model_actual) .^ 2, "all");
SSE_good_V0i = sum((V0i_actual - V0i_model) .^ 2, "all");
SSE_actual = sum((V0i_actual - V0i_model_actual) .^ 2, "all");

% Calculates percent error
error_V0i = abs((V0i - V0i_actual) ./ V0i_actual .* 100);
error_VMax = abs((Vmax_good - Vmax_actual) ./ Vmax_actual .* 100);
error_Km = abs((Km_good - Km_actual) ./ Km_actual .* 100);

%% ____________________
%% FORMATTED TEXT/FIGURE DISPLAYS
% disps all calculated values

fprintf("The SSE with the given Vmax and Km values is %.3f\n", SSE_good_VMax_Km);
fprintf("The SSE with the given V0i values is %.3f\n", SSE_good_V0i);
fprintf("The SSE with all given values is %.3f.\n", SSE_actual)
for i = 1:numel(error_V0i)
    fprintf("The error with the given Vmax and Km values for V0%d is %.3f percent\n", i, error_V0i(i));
end
fprintf("The error with VMax is %.3f\n", error_VMax);
fprintf("The error with Km is %.3f\n", error_Km);

% Displays the graph of our V0i compared to the actual model
figure(3)
plot(concentration,V0i,'mo',concentration,V0i_model_actual,'c:')
title('Given Model Compared to Experimental V0i')
xlabel('S (micromols)')
ylabel('V0i (micromol/sec)')
legend('Our Data','Actual Model','Location','best')

% Displays the graph of the actual V0i compared to our model.
figure(4)
plot(concentration,V0i_actual,'mo',concentration,V0i_model,'c:')
title('Actual V0i Compared to our model')
xlabel('S (micromols)')
ylabel('V0i (micromol/sec)')
legend('Actual Data','Model','Location','best')

%% ____________________
%% RESULTS

%% ____________________
%% ACADEMIC INTEGRITY STATEMENT
% We have not used source code obtained from any other unauthorized
% source, either modified or unmodified. Neither have we provided
% access to my code to another. The program we are submitting
% is our own original work.



