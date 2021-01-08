%Computes the homography matrix between the source coordinates to the
%destination coordinates. The output is a 3x3 projection matrix calculated
%using Single Value Decomposition. All parameters are nx1 vectors where
%n >= 4

%NOTE: This code was provided by PRG Robotics Group, through the CMSC 426
%Computer Vision Class at the University of Maryland. It will be re-written
%at a later date. 
function homography = estimateHomography(sourceX, sourceY, destX, destY)
    
    Temp = zeros(length(sourceX(:))*2,9);
    
    for pointIndex = 1:length(sourceX)
        
        a = [sourceX(pointIndex), sourceY(pointIndex), 1];
        b = [0 0 0];
        c = [destX(pointIndex); destY(pointIndex)];
        d = -c*a;
        
        A(((pointIndex - 1) * 2 + 1):((pointIndex -1) * 2 + 2), 1:9) = ...
            [[a b; b a] d];
    end
    
    [~,~,V] = svd(A);
    roughH = V(:,9);
    homography = reshape(roughH,3,3)';
    
end