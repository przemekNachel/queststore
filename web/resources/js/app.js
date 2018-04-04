let dropdown_toggle = document.querySelector('.dropdown');

document.addEventListener('click', function (event) {
    let className = event.target.className;

    if (className === "welcome-box") {
        dropdown_toggle.classList.toggle('hidden');
        console.log('toggle');
    } else {
        dropdown_toggle.classList.add('hidden');
        console.log('remove')
    }

});
