%Loads images and masks from masks.mat into a useable format. N images are
%chosen for training, and the rest are used for testing
function [testing, training, trainingMasks] = LoadImages(numTrainingImages)
    load('masks.mat')
    
    %create arrays of images and masks from masks.mat
    images = {im1, im2, im3, im4, im5, im6, im7, im8, im9, im10, im11, im12, im13, im14, ...
        im15, im16, im17, im18, im19, im20, im21, im22, im23};

    masks = {mask1, mask2, mask3, mask4, mask5, mask6, mask7, mask8, mask9, mask10, ...
        mask11, mask12, mask13, mask14, mask15, mask16, mask17, mask18, mask19, ...
        mask20, mask21, mask22, mask23};
    
    %chose N random images and masks for training
    randomIndicies = randperm(length(images));
    testing = images(randomIndicies(1:numTrainingImages));
    trainingMasks = masks(randomIndicies(1:numTrainingImages));
    
    
    %use the remaining images for training
    training = images(randomIndicies(numTrainingImages:end));
    
end