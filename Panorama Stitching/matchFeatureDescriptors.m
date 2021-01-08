%Calculates the distance between two feature descriptor vectors, passed in
%as descriptor structs, and matches the corresponding features with the
%least distance. If the ratio between the first and second smallest matched
%distance is larger than the threshold, the feature is considered an
%unreliable match and discarded. Distances are calculated using the sum of
%two feature's square differences (SSD). Matches are returned in a Nx4
%matrix with columns in the form of: [img1Y img1X img2Y img2X].
function matches = matchFeatureDescriptors(im1Descriptors, im2Descriptors, threshold)

    numDescriptors = length(im1Descriptors.descriptor(1,:));
    matches = zeros(numDescriptors, 4);
    
    %Find best feature correspondences using SSD
    dist = sum((im1Descriptors.descriptor(:,1)...
        - im2Descriptors.descriptor(:,1)).^2);
    lowestDistances = [dist,1,1;dist,1,1];
    
    for d1Index = 1:numDescriptors
   
        descriptorToMatch = im1Descriptors.descriptor(:,d1Index);
        
        for d2Index = 2:numDescriptors
            
            descriptorToCompare = im2Descriptors.descriptor(:,d2Index);
            
            dist = sum((descriptorToMatch - descriptorToCompare).^2);
            
            if (dist < lowestDistances(1,1))
                lowestDistances(2,:) = lowestDistances(1,:);
                lowestDistances(1,:) = [dist, d1Index, d2Index];
            else
                if (dist < lowestDistances(2,1))
                    lowestDistances(2,:) = [dist, d1Index, d2Index];
                end
            end
        end
        
        ratio = lowestDistances(1,1) / lowestDistances(2,1);
        if (ratio < threshold)
            matches(d1Index,:) = [im1Descriptors.location(lowestDistances(1,2),:)...
                im2Descriptors.location(lowestDistances(1,3),:)];
        end
        
        lowestDistances = ones(2,3) .* 100;
    end
    
    matches((matches(:,1) == 0),:) = [];
end