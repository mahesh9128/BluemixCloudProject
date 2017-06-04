window.onload = function() {

    loadDocuments();

    //deleting a file 
    document.getElementById("delfiletbtn").onclick = function() {
        var filename = document.getElementById("fileDel");
        var fileversion = document.getElementById("fileDelver");
        var filedata = {};
        filedata["name"] = filename.value;
        filedata["version"] = fileversion.value;
        var jsonStr = JSON.stringify(filedata);
        //ajax call
        var xmlhttp;
        if (window.XMLHttpRequest)
            xmlhttp = new XMLHttpRequest();
        else
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        xmlhttp.open("POST", "DeleteServlet", true);
        xmlhttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xmlhttp.send(jsonStr);
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                alert(xmlhttp.responseText);
                window.location.href = "./index.html";
            }
        };
    };

    //downloading a file 
    document.getElementById("downloadbtn").onclick = function() {

        var filedata = {};
        filedata["name"] = document.getElementById("fileDown").value;
        filedata["version"] = document.getElementById("fileDownver").value;

        var jsonStr = JSON.stringify(filedata);
        if (window.XMLHttpRequest)
            xmlhttp = new XMLHttpRequest();
        else
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        xmlhttp.open("POST", "DownloadServlet", true);
        xmlhttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xmlhttp.send(jsonStr);
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                if (xmlhttp.responseText == "fail") {
                    alert("File doesnt exist");
                } else {
                    downloadFile(document.getElementById("fileDown").value, xmlhttp.responseText);
                }

            }
        };
    };

    //uploading a file 
    document.getElementById('upload-form').onsubmit = function() {
        var form = document.getElementById('upload-form');
        var fileInput = document.getElementById("myfileinput");
        var fileList = fileInput.files;
        var formData = new FormData(form);

        var time = fileList[0].lastModified;

        formData.append('lastTime', fileList[0].lastModified);

        if (window.XMLHttpRequest)
            xmlhttp = new XMLHttpRequest();
        else
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");


        // Add any event handlers here...
        xmlhttp.open('POST', "UploadServlet", true);
        xmlhttp.send(formData);
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                alert(xmlhttp.responseText);
                window.location.href = "./index.html";

            }
        };

        return false; // To avoid actual submission of the form
    }

}


function uploadFile() {
    var form = document.getElementById('upload-form');

    form.onsubmit = function() {
        var fileInput = document.getElementById("myfileinput");
        var files = fileInput.files;
        var formData = new FormData(form);


        formData.append('lastime', files[0].lastModifiedDate);
        var xhr = new XMLHttpRequest();
        // Add any event handlers here...
        xhr.open('POST', form.getAttribute('action'), true);
        xhr.send(formData);
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                window.location.href = "./index.html";
                alert(responseText);
            }
        };

        return false; // To avoid actual submission of the form
    }
}

//loading the initial data
function loadDocuments() {
    var xmlhttp;
    if (window.XMLHttpRequest)
        xmlhttp = new XMLHttpRequest();
    else
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    xmlhttp.open("GET", "GetAllDocsServlet", true);
    xmlhttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xmlhttp.send();
    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            var contdet = document.getElementById('contdetail');
            contdet.innerHTML = xmlhttp.responseText;
        }
    };
}

function downloadFile(fname, text) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', fname);
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
}