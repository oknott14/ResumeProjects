%Computes a single gaussian distribution on the nx3 matrix pixels.
%pixels is of the form [red channel, green channel, blue channel]
function results = singleGaussian(images, pixels)

    pixels = double(pixels);
    mu = [mean(pixels(:,1)); mean(pixels(:,2)); mean(pixels(:,3))];
    sigma = cov(pixels);
    numImages = length(images);
    
    %compute p(Orange|x) for each pixel in the testing images
    %reduces pixel brightness for each x under the threshold by 75%
    for imageIndex = 1:numImages
        currImage = images{imageIndex};
        [height, width,~] = size(currImage);
        cModel = zeros(size(currImage,1:2));
        for row = 1:height
            for col = 1:width
                x = double([currImage(row, col, 1); currImage(row, col, 2); currImage(row, col, 3)]);
                px = mvnpdf(x,mu,sigma);
                cModel(row,col) = px;
            end
        end
        
        threshold = .25 * max(cModel(:));
        
        for row = 1:height
            for col = 1:width
                %If the image is not considered Orange, lower it's
                %brightness
                if cModel(row,col) < threshold
                    currImage(row, col, :) = currImage(row, col, :) .* .25;
                end
            end
        end
        images{imageIndex} = currImage;
    end
    
    results = images;
end