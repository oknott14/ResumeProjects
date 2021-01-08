%% Main / Driver
%This script runs the single Gaussian and Gaussian Mixture Model color
%segmentation methods.

% add all required folders
close all;
addpath('.\results')

%Get images and masks
%NOTE: Do not pass a parameter greater than 23
[trainingImgs, testingImgs, masks] = LoadImages(18);

%Isolate orange pixels
orangePixels = getOrange(trainingImgs, masks);

%Compute single gaussian color segmentation
singleGaussianResults = singleGaussian(testingImgs, orangePixels);

%Show results
for rNum = 1:length(testingImgs)
    image = testingImgs{rNum};
    result = singleGaussianResults{rNum};
    %% USE SUB PLOTS AND TITLES TO MAKE THE OUTPUT A LOT MORE READABLE.
    fig = imfuse(image, result, 'montage');
end
