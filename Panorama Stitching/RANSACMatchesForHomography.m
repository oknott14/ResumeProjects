%Cleans rough matches using a Random Sampling Consensus algorithm. The
%algorithm uses 4 random match pairs and computes a homography between
%coordinate points. The algorithm converges when 90% of the matches are
%kept or the maximum number of iterations is reached. The set with the most
%inliers is always kept. An nx4 matrix of matches and the 3x3 homography
%matrix is returned.
function [matches, homography] = RANSACMatchesForHomography(roughMatches, nMax, threshold)
    
    numMatches = size(roughMatches,1);
    numRequestedInliers = ceil(numMatches * 0.9);
    
    permMatches = roughMatches(randperm(numMatches), :);  
    
    bestInliers = zeros(2,numRequestedInliers);
    
    index = 1; 
    startIndex = 1;
    endIndex = startIndex + 3;
    numInliers = 1; 
    
    while (i <= nMax && endIndex < numRequestedInliers)
       
        %Chose 4 random matches
        endIndex = startIndex + 3; 
        destX = permMatches(startIndex:endIndex,2);
        destY = permMatches(startIndex:endIndex,1);
        sourceX = permMatches(startIndex:endIndex,4);
        sourceY = permMatches(startIndex:endIndex,3);
        
        startIndex = startIndex + 4;
        endIndex = startIndex + 3;
        
        homography = estimateHomography(sourceX, sourceY, destX, destY);
        
        %Compute inliers            
        im1X = roughMatches(:,2);
        im1Y = roughMatches(:,1);
        im2X = roughMatches(:,4);
        im2Y = roughMatches(:,3);
        
        pointMatrix = [im2X'; im2Y'; ones(1, numMatches)];
        projResult = homography * pointMatrix;
        projResult = projResult ./ [projResult(3,:);projResult(3,:);projResult(3,:)];
        
        im1EstX = projResult(1,:)';
        im1EstY = projResult(2,:)';
        
        SSD = [(im1EstX - im1X).^2 + (im1EstY - im1Y).^2 roughMatches];
        
        currentInliers = SSD(SSD(:,1) < threshold, 2:end);
        
        if (length(currentInliers) > length(nonzeros(bestInliers)))
            bestInliers = currentInliers;
        end
    end
    
    destX = bestInliers(:,2);
    destY = bestInliers(:,1);
    sourceX = bestInliers(:,4);
    sourceY = bestInliers(:,3);
    
    homography = estimateHomography(sourceX, sourceY, destX, destY);
    matches = bestInliers;
end