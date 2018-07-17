// För VG - Bildspel
setInterval((function () {
    var slideIndex = 0;
    var imageList = document.getElementsByClassName("image-slider-img");
    var imageCount = document.querySelector("#image-count");
    imageCount.innerHTML =  "1/" + imageList.length;

    return function() {
        // Ta bort klasser
        var i;
        for(i = 0; i < imageList.length; i++) {
            imageList[i].classList.remove("image-active");
        }

        // Redogör att det är ett korrekt index
        slideIndex = (slideIndex + 1) % imageList.length;
        imageList[slideIndex].classList.add("image-active");
        imageCount.innerHTML = (slideIndex + 1) + "/" + imageList.length;
    }
}()), 5000);
