document.addEventListener("DOMContentLoaded" ,() =>{
    
    const signin=document.getElementById("form");
    signin.addEventListener("submit",(e)=>{
        e.preventDefault();
        const user=document.getElementById("username").value;
        const pass=document.getElementById("password").value;

        if(user=="cafedelight" && pass=="12345"){
            signin.reset();
            window.open("order.html","_self");

        }
        else
            alert("Incorrect username or password");
            signin.reset();
        
    })
}) 

