// common variables
var iBytesUploaded = 0;
var iBytesTotal = 0;
var iPreviousBytesLoaded = 0;
var sResultFileSize = '';

function bytesToSize(bytes) {
    var sizes = ['Bytes', 'KB', 'MB'];
    if (bytes == 0) return 'n/a';
    var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
    return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
};

function fileSelected() {

    document.getElementById('upload_response').style.display = 'none';
    var oFile = document.getElementById('transfer_file').files[0];
    
    var oReader = new FileReader();
        oReader.onload = function(e){

    var oImage;
        oImage.src = e.target.result;
    };

    oReader.readAsDataURL(oFile);
}

function startUploading() {
    iPreviousBytesLoaded = 0;
    document.getElementById('upload_response').style.display = 'none';
    document.getElementById('progress_percent').innerHTML = '';
    var oProgress = document.getElementById('progress');
    oProgress.style.display = 'block';
    oProgress.style.width = '0px';

    // get form data for POSTing
    var vFD = new FormData(document.getElementById('upload_form')); 

    // create XMLHttpRequest object, adding few event listeners, and POSTing our data
    var oXHR = new XMLHttpRequest();        
    oXHR.upload.addEventListener('progress', uploadProgress, false);
    oXHR.addEventListener('load', uploadFinish, false);
    oXHR.open('POST', 'UploadFileServlet',true);
    oXHR.send(vFD);
}

function uploadProgress(e) {
    if (e.lengthComputable) {
        iBytesUploaded = e.loaded;
        iBytesTotal = e.total;
        var iPercentComplete = Math.round(e.loaded * 100 / e.total);
        var iBytesTransfered = bytesToSize(iBytesUploaded);

        document.getElementById('progress_percent').innerHTML = iPercentComplete.toString() + '%';
        document.getElementById('progress').style.width = (iPercentComplete * 4).toString() + 'px';
        if (iPercentComplete == 100) {
            var oUploadResponse = document.getElementById('upload_response');
            oUploadResponse.innerHTML = '<h1>Παρακαλώ περιμένετε...πραγματοποιείται επεξεργασία</h1>';
            oUploadResponse.style.display = 'block';
        }
    } else {
        document.getElementById('progress').innerHTML = 'αδύνατο να υπολογιστεί';
    }
}

function uploadFinish(e) {
    var oUploadResponse = document.getElementById('upload_response');
    oUploadResponse.innerHTML = e.target.responseText;
    oUploadResponse.style.display = 'block';
    document.getElementById('progress_percent').innerHTML = '100%';
    document.getElementById('progress').style.width = '400px';
}