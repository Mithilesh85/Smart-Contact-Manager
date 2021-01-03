console.log("this is script file")


const togglesidebar = ()  =>{

 if($('.sidebar').is(":visible")){
 
// do hide
 
 $(".sidebar").css("display","none");
 $(".sidebar").css("margin-left" , "0%");
 
 
 }else
 {
 
// do show
 
 
 $(".sidebar").css("display","block");
 $(".sidebar").css("margin-left", "0%");
 
 }

};