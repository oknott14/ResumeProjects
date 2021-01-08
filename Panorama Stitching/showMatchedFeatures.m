%Displays the two images in a montage next to eachother, plotting the
%matched point locations and a line between them.
function showMatchedFeatures(image1, image2, matchedPoints)

    [~, width1, ~] = size(image1);
    matchedPoints(:,4) = matchedPoints(:,4) + width1;
    
    imgOverlay = imfuse(image1, image2, 'montage');
    figure; imshow(imgOverlay); hold on;
    
    Y = [matchedPoints(:,1); matchedPoints(:,3)];
    X = [matchedPoints(:,2); matchedPoints(:,4)];
    
    scatter(X, Y, 'red');
    
    for point = 1:size(matchedPoints,1)
        
       plot([matchedPoints(point,2) matchedPoints(point,4)],...
           [matchedPoints(point,1) matchedPoints(point,3)], '-y');
       
    end
    
    hold off;

end