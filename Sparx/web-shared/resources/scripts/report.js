// ------------------------------------------------------
// Toggle the display of the document element
// ------------------------------------------------------
function toggleDisplay(id, expandClassName, collpaseClassName)
{
    var sourceElement = document.getElementById(id);

    id = id + "-data";
    var el = document.getElementById(id).style;
    if(el.display == "none")
    {
        sourceElement.className = collpaseClassName;
        el.display = "";
    }
    else if(el.display == "" || el.display == "block")
    {
        sourceElement.className = expandClassName;
        el.display = "none";
    }
}