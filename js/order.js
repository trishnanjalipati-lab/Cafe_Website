document.addEventListener("DOMContentLoaded" ,() =>{
const details=document.getElementById("order");
const ordercontainer=document.querySelector(".order-container")
function load(){
        return JSON.parse(localStorage.getItem("order"))|| []
    }
details.addEventListener("submit",(e)=>{
        e.preventDefault();
        const dish=document.getElementById("item");
        const qty=document.getElementById("qty");
        console.log(dish,qty)
        if(dish.value=="Select"){
            alert("Provide details");
            return
        }
        if(!qty.value){
            qty.value=1;
        }
       
        alert("Order Placed Successfully")
        save(dish,qty);
    })
      
    function save(dish,qty){
        const order=load()
        order.push({"item":dish.value,"qty":qty.value});
        localStorage.setItem("order",JSON.stringify(order));
        show();
    }
    function show(){
        ordercontainer.innerHTML = "";
        const order=load();
        order.forEach((order,index) => {
            const orderdiv=document.createElement("div");
            orderdiv.className="order";
            const item=document.createElement("label");
            item.textContent=order.item;
            const qty=document.createElement("label");
            qty.textContent=order.qty;
            const sno=document.createElement("label");
            sno.textContent=index+1;
            orderdiv.appendChild(sno);
            orderdiv.appendChild(item);
            orderdiv.appendChild(qty);
            ordercontainer.appendChild(orderdiv);

        });
            
       
    }
    
    show();
    })
