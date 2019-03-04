/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

var latest_release_url = '/api/builds/latest';

$(document).ready(function() {

    $.ajax({
        url: latest_release_url
    }).done(function(data) {

        $('#download_link').attr('href', data.runtime.driver_location);
        $('#download_link_size_label').text('(' + Math.floor(data.runtime.size_in_bytes / 1048576) + ' MB)');

    });
});


$('#code_container').click(function(event) {
    console.log("function called");
    target = event.currentTarget;
    copy_original = $('#copy_message').html();
    console.log(copy_original);
    event.preventDefault();
    window.getSelection().selectAllChildren(target);
    if(document.execCommand('copy')) {
        window.getSelection().removeAllRanges(); 

        $('#code_container').css("background-color", "#EFF4CA");
        $('#code_container').css("color", "#ABD155");
        $('#copy_message').text("Copied to clipboard!");

        setTimeout(function(){
            $('#code_container').css("background-color", "#F1F4FE");
            $('#code_container').css("color", "#100F15");
            $('#copy_message').html(copy_original);
        }, 1000);

    } else {
        alert('To copy press CTRL + C');
    }

});