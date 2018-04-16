let dropdown_toggle = document.querySelector('.dropdown');
let switch_prefix = 'switch-';
let last_active_content = document.getElementById('dashboard');
let selected_option = document.getElementById('switch-dashboard');

function switchContent(classID) {
    last_active_content.classList.add('hidden');

    last_active_content = document.getElementById(classID.replace(switch_prefix, ''));

    last_active_content.classList.remove('hidden');

    selected_option.classList.add('selected');

    document.querySelector('.content-header-label').textContent = selected_option.childNodes[3].textContent;


}

document.addEventListener('click', function (event) {
    let className = event.target.className;
    let classID = event.target.id;
    let parentClassID = event.target.parentNode.id;
    if (classID.includes(switch_prefix)) {
        selected_option.classList.remove('selected');
        selected_option = event.target;
        switchContent(classID);
    }
    if(parentClassID.includes(switch_prefix)) {
        selected_option.classList.remove('selected');
        selected_option = event.target.parentNode;
        switchContent(parentClassID);
    }

    if (className === "welcome-box") {
        dropdown_toggle.classList.toggle('hidden');
    } else {
        dropdown_toggle.classList.add('hidden');
    }

});
