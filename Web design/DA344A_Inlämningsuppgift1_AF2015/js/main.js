initSelect();
toggleMenu();

function toggleMenu() {
    var toggle = document.querySelector("#menu-toggle");
    var nav = document.querySelector("nav");

    toggle.addEventListener("click", function() {
        if(nav.className === "show") {
            nav.className = "hide";
        } else {
            nav.className = "show";
        }

    }, false);
}

function initSelect() {
    var select = document.querySelector("select");
    select.addEventListener("change", function() {
        setStylesheet(this.value, 1);
    }, false);

    var selectedStyle = localStorage.getItem("stylesheet");
    if(selectedStyle != null) {
        setStylesheet(selectedStyle, 1);
        select.value = selectedStyle;
    }
}

function setStylesheet(cssFile, cssLinkIndex) {
    localStorage.setItem("stylesheet", cssFile);

    var oldLink = document.getElementsByTagName("link").item(cssLinkIndex);

    var newLink = document.createElement("link");
    newLink.setAttribute("rel", "stylesheet");
    newLink.setAttribute("type", "text/css");
    newLink.setAttribute("href", "css/" + cssFile + "_stylesheet.css");

    document.getElementsByTagName("head").item(0).replaceChild(newLink, oldLink);
}
