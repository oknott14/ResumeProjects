function orangePixels = getOrange(imgs, masks)

    r = [];
    g = [];
    b = [];
    
    for idx = 1:length(imgs)
       
        cImg = imgs{idx};
        [row, col] = find(double(masks{idx}));
        cOrange = cImg(row(:), col(:), :);
        r = [r; reshape(cOrange(:,:,1), [], 1)];
        g = [g; reshape(cOrange(:,:,2), [], 1)];
        b = [b; reshape(cOrange(:,:,3), [], 1)];
        
    end

    orangePixels = [r, g, b];
end