function panorama = createPanorama(images, homographies)
    numImages = length(images);
    
    %Compute new projection points and world limits
    finalPts = cell(1,numImages-1);
    lims = ones(2,2);
    [anchorHeight, anchorWidth] = size(images{1},1:2);
    lims(:,2) = [anchorWidth; anchorHeight];
    
    for imgIndex = 2:numImages
        
        image = images{imgIndex};
        trans = homographies{imgIndex};
        [h,w] = size(image,1:2);
        [Y, X] = meshgrid(1:h,1:w);
        Y = Y(:);
        X = X(:);
        pts = [X, Y, ones(length(X),1)]';
        res = (trans * pts)';
        res = round(res ./ [res(:,3), res(:,3), res(:,3)]);
        finalPts{imgIndex-1} = [X Y res(:,1:2)];
        
        xMin = min(res(:,1));
        yMin = min(res(:,2));
        xMax = max(res(:,1));
        yMax = max(res(:,2));
        
        if (lims(1,1) > xMin)
            lims(1,1) = xMin;
        end
        if (lims(1,2) < xMax)
            lims(1,2) = xMax;
        end
        if (lims(2,1) > yMin)
            lims(2,1) = yMin;
        end
        if (lims(2,2) < yMax)
            lims(2,2) = yMax;
        end
    end
    
    %Create panoramic image (2 px buffer so images dont hit zero)
    xWorldLim = round(abs(lims(1,2) - lims(1,1)) + 2);
    yWorldLim = round(abs(lims(2,2) - lims(2,1)) + 2);
    
    panorama = zeros(yWorldLim, xWorldLim, size(images{1},3));
    
    startX = abs(lims(1,1)) + 1;
    startY = abs(lims(2,1)) + 1;
    
    panorama(startY:startY+anchorHeight-1, startX:startX+anchorWidth-1,:) = ...
        images{1}(:,:,:); %Set anchor Image
    
    for imgIndex = 2:numImages
        
        image = images{imgIndex};
        points = finalPts{imgIndex-1};
        numPoints = size(points, 1);
        
        for currPoint = 1:numPoints
            
            X = startX + points(currPoint,3);
            Y = startY + points(currPoint,4);
            x = points(currPoint,1);
            y = points(currPoint,2);
            
            try
                panorama(Y,X,:) = image(y,x,:);
            catch e
                continue;
            end
        end
    end
end