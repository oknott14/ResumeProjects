%Takes a cell array of Image structs and a requested number of descriptors
%to fine the N strongest, equally dispeersed, feature descriptors in each
%image. From the detected features, an adaptive non-maximal supression
%(ANMS) algorithm ensures they are equally spread across the image. The
%resulting features are described with a 81x1 vector sampled from a 41x41
%gaussian blurred sub-sample, taken from the black and white image.
function descriptors = getFeatureDescriptors(images, nDescriptors)
    numImages = length(images);
    descriptors = cell(1,numImages);
    descriptorRadius = 9;
    descriptorLength = (descriptorRadius)^2;
    
    %Get the N best equally distributed features from each image using an
    %adaptive non-maximal supression (or ANMS) algorithm.
    for imgIndex = 1:numImages
        
        tempImage = rgb2gray(images{imgIndex});
        [h,w,~] = size(tempImage);
        cImage = tempImage(20:h-21,20:w-21,:);
        cornerProbabilities = cornermetric(cImage);
        
        %Begin ANMS
        localMaximas = imregionalmax(cornerProbabilities, 8);
        [row, col] = find(localMaximas);
        numStrongest = length(row);
        
        R = ones(numStrongest,3); %[R, row, col]
        ED = size(cImage,1)^2 + size(cImage,2)^2; %Expected Distance
        R(:,1) = ED;
        
        for i = 1:numStrongest
            for j = 1:numStrongest
                % calculate the distance between the best strong corners
                % and each other strong corner found.
                if (cornerProbabilities(row(j), col(j)) > cornerProbabilities(row(i), col(i)))
                    ED = (col(j) - col(i))^2 + (row(j) - row(i))^2;
                end
                % Save the shortest distances
                if (ED < R(i,1))
                    R(i,:) = [ED, row(i), col(i)];
                end
            end
        end
        
        [~,idx] = sort(R(:,1), 'descend');
        R = R(idx,:);
        nBestCorners = R(1:nDescriptors, 2:end) + 20;
        
        %Create feature descriptors around corners
        cDescriptors = zeros(descriptorLength, nDescriptors);
        
        for featIndex = 1:nDescriptors
            
            center = nBestCorners(featIndex,:);
            frow = center(1);
            fcol = center(2);
            featureSample = tempImage((frow-20):(frow+20),(fcol-20):(fcol+20),:);
            filter = fspecial('gaussian', 41,41);
            filteredSample = imfilter(featureSample, filter, 100, 'replicate');
            descriptorSample = imresize(filteredSample, ...
                [descriptorRadius descriptorRadius]);
            descriptorVector = double(reshape(descriptorSample,...
                descriptorLength,1));
            vectorMean = mean(descriptorVector);
            vectorStd = std(descriptorVector);
            
            if (vectorStd ~= 0)
                cDescriptors(:,featIndex) = (descriptorVector - vectorMean) ./ vectorStd;
            end
                
        end
        
        descriptors{imgIndex} = struct('location', {nBestCorners},...
            'descriptor', cDescriptors, 'radius', descriptorRadius);
    end

end