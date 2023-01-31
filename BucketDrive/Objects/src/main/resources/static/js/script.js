function showPass1() {
    var x = document.getElementById("password");
    if (x.type === "password") {
        x.type = "text";
    } else {
        x.type = "password";
    }
}

function showPass2() {
    var x = document.getElementById("passwordRepeat");
    if (x.type === "password") {
        x.type = "text";
    } else {
        x.type = "password";
    }
}

function checkPass() {
    if (document.getElementById('password').value == document.getElementById('passwordRepeat').value) {
        document.getElementById('signUp').disabled = false;
    } else {
        document.getElementById('signUp').disabled = true;
    }
}