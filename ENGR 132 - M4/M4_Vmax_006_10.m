function [Vmax]= M4_Vmax_006_10(V0i, concentration, E_name)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ENGR 132 
% Program Description 
% This function take the average V0i values and generates a Vmax for that
% dataset
%
% Function Call
% M2_Vmax_006_10
%
% Input Arguments
% V0i is the average V0i calculated from the step previous. Concentration 
% is the amount
% of time in seconds. E_name is the enzyme name
%
% Output Arguments
% Vmax is the max V for each test
%
% Assignment Information
%   Assignment:     M2, Problem 1
%   Team member:    Creigh Dircksen, cdirckse@purdue.edu 
%                   Winston Lin, wylin@purdue.edu
%                   Joe Pahoresky, jpahores@purdue.edu
%                   Taylor Duchinski, tduchins@purdue.edu
%   Team ID:        006-10
%   Academic Integrity:
%     [] We worked with one or more peers but our collaboration
%        maintained academic integrity.
%     Peers we worked with: Name, login@purdue [repeat for each]
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% ____________________
%% INITIALIZATION


%% ____________________
%% CALCULATIONS
coeffs=polyfit(1./concentration,1./V0i,1);
% This calculates the y intercept and the slope of the linear
% representation of 1/S and 1/V0i.
Vmax=1/coeffs(2);
% This sets Vmax to be the reciprocal of the y axis of the above plot
%% ____________________
%% FORMATTED TEXT/FIGURE DISPLAYS
fprintf('The Vmax for Enzyme %s is %0.3f\n',E_name,Vmax)
% This prints teh name of the enzyme and the Vmax for it


%% ____________________
%% RESULTS


%% ____________________
%% ACADEMIC INTEGRITY STATEMENT
% We have not used source code obtained from any other unauthorized
% source, either modified or unmodified. Neither have we provided
% access to my code to another. The program we are submitting
% is our own original work.



