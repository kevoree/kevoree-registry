require('es6-promise').polyfill();

var fetch = require("isomorphic-fetch");

function callget(url, resultDiv) {
    fetch(url)
        .then(function(response) {
            if(response.status >= 400) {
                resultDiv.innerHTML += "Bad response from server";
                console.log(response);
            } else {
                return response.json()
            }
        }).then(function (json) {
            resultDiv.innerHTML += JSON.stringify(json);
        });
}


function callapi() {
    console.log("ok");
    var method = document.getElementsByName('action')[0].value,
        url = document.getElementsByName('url')[0].value,
        resultDiv = document.getElementById("result");

    if(method === 'GET') {
        resultDiv.innerHTML = "HTTP GET on " + url;
        callget(url, resultDiv);
    } else {
        resultDiv.innerHTML = "Unhandled http action " + method;
    }
}


document.getElementsByName('submit')[0].addEventListener('click', callapi);
