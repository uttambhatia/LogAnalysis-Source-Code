<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">

<head>
<link rel="stylesheet" type="text/css" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.19/css/dataTables.jqueryui.min.css" />
<link rel="stylesheet" href="/resource/css/logParser.css"></link>
<link rel="stylesheet" type="text/css" href="webjars/bootstrap/3.3.7/css/bootstrap.min.css" />
<script src="/webjars/jquery/3.1.1/jquery.min.js"></script>
<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="/webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/resource/js/logParser.js"></script>
</head>

<body>

	<nav class="navbar navbar-inverse">
		<div class="container">
			<div id="navbar" class="navbar-header">
				<a href="#" class="navbar-brand active" onclick="changeTab('view',this)">Log Event(s)</a> 
			</div>
			<div class="navbar-header">
				<a class="navbar-brand" href="#" onclick="changeTab('enter',this)">Start
					Log Parser</a>
			</div>
		</div>
	</nav>

	<div class="container">

		<div style="width: 20%;" class="starter-template">
			<h1>Log File Processing Engine</h1>
			<span onclick="switchTab();" class="button"
				style="width: auto; float: right;">Start Process</span>
		</div>
		
		<div style="width: 20%; margin: auto;">
			<div class="loader" id = "dataLoading" style="display: none;"></div>
		</div>
		
		<div id="enterVehicle" class="enterVehicle">

			<div id="preparesData">
				<h3>Prepare Data</h3>
				File Name* : <input type="text" placeholder =".json" name="newfileName" id="newfileName">
				<input type="text" style='width:15%;' placeholder ='File Size' name="fileSize" id="fileSize"> :MB
				<button onclick="prepareData()" id='dataPreparing'>Prepare</button>
			</div>
			<br> <br>

			<div>
				<h3>Process Data</h3>
				File name*
				 : <input type="text" placeholder =".json" name="fileName" id="fileName">
				<button onclick="startFileProcessor()" id='dataProcessing'>Process</button>
			</div>
		</div>

		<div id="viewReport">
		  <div class="container" align="center">
				<table class="table table-bordered" id="firewalldata">
					<thead>
						<tr>
							<th>Event ID</th>
							<th>Duration</th>
							<th>Host</th>
							<th>Type</th>
							<th>Alert</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>
		</div>

	</div>

</body>

</html>
