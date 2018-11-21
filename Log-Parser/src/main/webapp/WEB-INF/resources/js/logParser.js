   var dataTable;
   var dataLoadTimer;
   
   var dataPreparingTimer;
   var dataProcessingLoadTimer;

    $( document ).ready(function() {
       $('#enterVehicle').hide();
       $('#viewReport').show();

       viewReport();
       
    });
	
    function changeTab(tab){
    	
    	if(tab == 'enter'){
    		switchTab();
    	} else if(tab == 'view'){
    		
    		reloadDataTable();
    		
    		 clearInterval(dataPreparingTimer);
    		 clearInterval(dataProcessingLoadTimer);
    	}
    	
    }
    
	function switchTab() {
		
		 $('.starter-template').html('');
		 $('#enterVehicle').show();
	     $('#viewReport').hide();
	     
	     $('#dataLoading').css('display','none');
	     clearInterval(dataLoadTimer);
	     
	     dataPreparingStatus();
	     dataProcessingStatus();
	     dataPreparingTimer = setInterval(dataPreparingStatus,5000);
	     dataProcessingLoadTimer = setInterval(dataProcessingStatus,4000);
	     
	}

	function reloadDataTable(){
		
		$('.starter-template').html('<h1>Log Event(s)</h1>');
		$('#viewReport').show();
		$('#enterVehicle').hide();
		
		dataTable.ajax.reload();
		processedDataLoadingStatus();
		dataLoadTimer = setInterval(processedDataLoadingStatus,10000);
		
	}
	
	function viewReport() {
		
		$('.starter-template').html('<h1>Log Event(s)</h1>');
		$('#viewReport').show();
		$('#enterVehicle').hide();
	    viewVehiclePoolReport();
	     
	    processedDataLoadingStatus();
	    dataLoadTimer = setInterval(processedDataLoadingStatus,10000);
	     
	}
	
	
	function viewVehiclePoolReport() {

		 dataTable =  $('#firewalldata').DataTable( 
	                {  
	                	"processing": true,
	                    "serverSide": true,
	                    "ajax": { 
	                       'url':'/eventList.json',
	                      },
	                    "columns": 
	                    [
	                        { "data": "eventId" },
	                        { "data": "eventDuration" },
	                        { "data": "type" },
	                        { "data": "host" },
	                        { "data": "alert" }
	                    ]
	        } );

	}
	
	
	function startFileProcessor() {

		var fileName = $('#fileName').val();

		$('#dataProcessing').addClass('dataProcessing');
    	$('#dataProcessing').text('');
    	
		var userProfile = $.ajax({
			type : "GET",
			url : '/startFileProcessor.json',
			data:{fileName:fileName},
			dataType : 'json'
		});

		userProfile.done(function(data) {
			alert("Submitted request for processing the file:: "+fileName);
		});

	}
	
	function prepareData() {

		var fileName = $('#newfileName').val();
		var fileSize = $('#fileSize').val();

		$('#dataPreparing').addClass('dataPreparing');
    	$('#dataPreparing').text('');
    	
		var userProfile = $.ajax({
			type : "GET",
			url : '/prepareJsonData.json',
			data:{fileName:fileName, fileSize:fileSize},
			dataType : 'json'
		});

		userProfile.done(function(data) {
			alert("Data Preparation in progress for file:: "+fileName);
		});

	}
	
	function processedDataLoadingStatus(){
		
		var fileName = $('#fileName').val();
		
		var userProfile = $.ajax({
			type : "GET",
			url : '/getDataParsingStatusForFile.json',
			data:{fileName:fileName},
			dataType : 'json'
		});

		userProfile.done(function(data) {
            if(data.status == 'SUCCESS'){
            	$('#dataLoading').css('display','none');
            	clearInterval(dataLoadTimer);
            }
            else{
            	dataTable.ajax.reload();
            	$('#dataLoading').css('display','block');
            }
		});
		
	}
	
function dataPreparingStatus(){
		
		var fileName = $('#newfileName').val();
		
		var userProfile = $.ajax({
			type : "GET",
			url : '/getDataPreparationStatusForFile.json',
			data:{fileName:fileName},
			dataType : 'json'
		});

		userProfile.done(function(data) {
            if(data.status == 'SUCCESS'  | data.status == null){
            	$('#dataPreparing').removeClass('dataPreparing');
            	$('#dataPreparing').text('Prepare');
            	clearInterval(dataLoadTimer);
            	
            	$('#newfileName').val('');
            	$('#fileName').val(fileName);
            }
            else{
            	$('#dataPreparing').addClass('dataPreparing');
            	$('#dataPreparing').text('');
            }
		});
		
	}

function dataProcessingStatus(){
	
	var fileName = $('#fileName').val();
	
	var userProfile = $.ajax({
		type : "GET",
		url : '/getDataParsingStatusForFile.json',
		data:{fileName:fileName},
		dataType : 'json'
	});

	userProfile.done(function(data) {
        if(data.status == 'SUCCESS' | data.status == null){
        	$('#dataProcessing').removeClass('dataProcessing');
        	$('#dataProcessing').text('Process');
        	clearInterval(dataLoadTimer);
        }
        else{
        	$('#dataProcessing').addClass('dataProcessing');
        	$('#dataProcessing').text('');
        }
	});
	
}