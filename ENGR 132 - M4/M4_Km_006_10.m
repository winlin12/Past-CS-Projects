function [Km] = M4_Km_006_10(V0i, concentation, E_name)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ENGR 132 
% Program Description 
% given data gathered from other UDF's, this code takes the initial
% velocity and S value and uses it to calculate
% the Km given thelinearized version of the initial speeds
%
% Function Call
% [Km] = M4_Km_006_10(V0i, S, E_name)
%
% Input Arguments
% V0i = initial velocity for a given dataset
% S_concentration = initial concentration for the test
% E_name = name of the enzyme 
%
% Output Arguments
% Km = the time when the concentration is half that at the vmax
%
% Assignment Information
%   Assignment:     MM2, Problem 1
%   Team member:    Creigh Dircksen, cdirckse@purdue.edu 
%                   Winston Lin, wylin@purdue.edu
%                   Joe Pahoresky, jpahores@purdue.edu
%                   Taylor Duchinski, tduchins@purdue.edu
%   Team ID:        006-10
%   Academic Integrity:
%     [NO] We worked with one or more peers but our collaboration
%        maintained academic integrity.
%     Peers we worked with: N/A
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% ____________________
%% INITIALIZATION

% No Initialization

%% ____________________
%% CALCULATIONS

coeffs = polyfit(1 ./ concentation, 1 ./ V0i, 1); % finds the linearized equation coefficients for the data
Km = coeffs(1)/coeffs(2); % calculates Km to be negative one over the x intercept
vmax = 1/coeffs(2); % calculates the vmax for model

%model calculation
model = (vmax .* concentation) ./ (Km + concentation);

%% ____________________
%% FORMATTED TEXT/FIGURE DISPLAYS

fprintf('%s Km value is %0.2f \n', E_name, Km) %prints the Km with the name of the enzyme

%displayed figure
figure()
plot(concentation, V0i,'ms', concentation, model, 'b-')
grid on
title("Enzyme " + E_name + " V0i")
xlabel('Initial [S] (uM)')
ylabel("V0i")
legend('Experimental Initial Velocity', 'Calculated Initial Velocity','Location','best')
% This plots V0i onto S and adds labels and a title

%% ____________________
%% RESULTS

%No Results

%% ____________________
%% ACADEMIC INTEGRITY STATEMENT
% We have not used source code obtained from any other unauthorized
% source, either modified or unmodified. Neither have we provided
% access to my code to another. The program we are submitting
% is our own original work.



