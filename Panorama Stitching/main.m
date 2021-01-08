%Main / Driver
%This script takes images from the images folder and creates a panoramic
%image using the best common features in each image. A feature is
%considered reliable / unreliable using a homographic projection and RANSAC
%outlier rejection method.

%USE THIS VARIABLE TO VISUALISE EVERY STEP (TRUE) OR 
%ONLY SEE THE RESULT(FALSE)
visualize = true;

%Load images from folder
imageSets = loadImages();
numSets = length(imageSets);
setIndex = 3;

%Control Variables
numDescriptors = 500;
featureMatchThreshold = .27;
RANSACthreshold = 400;
RANSACmax = 100;
for setIndex = 3:numSets
    
    currSet = imageSets{setIndex};
    setFeatures = getFeatureDescriptors({currSet.images}, numDescriptors);
    homographies = cell(1,currSet(1).length);
    homographies{1} = eye(3,3);
    for imgPairs = 2:currSet(1).length
        
        feats1 = setFeatures{imgPairs - 1};
        feats2 = setFeatures{imgPairs};
        
        roughPairMatches = ...
            matchFeatureDescriptors(feats1, feats2, featureMatchThreshold);
        [bestMatches, pairHomography] = RANSACMatchesForHomography(...
            roughPairMatches, RANSACmax, RANSACthreshold);
       
        homographies{imgPairs} = pairHomography * homographies{imgPairs-1};
        
        if (visualize)
            showMatchedFeatures(currSet(imgPairs - 1).images, ...
                currSet(imgPairs).images, bestMatches);
        end
    end
    
    panorama = createPanorama({currSet.images}, homographies);
    figure; imshow(panorama);
end



