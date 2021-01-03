function verifyData() {
    let content = document.getElementById('data_for_verification').value;
    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/verify', true);
    xhr.send(content);
    xhr.onload = function() {
        if (xhr.status !== 200) {
            alert(`Ошибка ${xhr.status}: ${xhr.statusText}`);
        } else {
            let response = JSON.parse(xhr.responseText).payload
            document.getElementById('data_for_verification').value = response;
        }
    };
}

function openVerification() {
    document.getElementById('vrf_txtarea_div').style = 'display: block';
    document.getElementById('vrf_send').style = '';
    document.getElementById('vrf_open').style = 'display: none';
}