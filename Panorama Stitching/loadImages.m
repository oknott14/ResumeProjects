%Reads images from all directories in the Images folder. Each sub directory
%of Images is a folder containing a set of n related images to stitch
%together.

%imageSets is an N by 1 cell array of structs, where N is the number of sub
%directories in Images. Eash cell in imageSets is a struct containing the
%directory name, the directory path, the number of images, and each image.
function imageSets = loadImages()

    addpath('.\Images');
    folder = dir(fullfile(('.\Images\*')));
    numDirs = length(folder);
    imageSets = cell(1,numDirs-2);
    
    for folderIndex = 3:numDirs
        currFolder = folder(folderIndex);
        
        if (currFolder.isdir)
            imgDir = dir(fullfile((horzcat(currFolder.folder, '\',...
                currFolder.name)), '*.jpg'));
            numImgs = length(imgDir);
            setImgs = cell(1,numImgs);
            
            for imgIndex = 1:numImgs
                setImgs{imgIndex} = im2double(imread(horzcat(imgDir(imgIndex).folder,...
                    '\', imgDir(imgIndex).name)));
            end
            
            imageSets{folderIndex-2} = struct('path', {imgDir.folder},...
                'length', {numImgs}, 'images', setImgs, 'name', {imgDir.name});
        end
        
    end
    
end