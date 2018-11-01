<!doctype html>
<html lang="en">
  <head>
  <meta http-equiv="Content-Type" content="text/html; charset=gb18030">
    <!-- Required meta tags -->
   <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="/css/bootstrap.min.css">
    <link rel="apple-touch-icon" sizes="57x57" href="/images/apple-icon-57x57.png">
	<link rel="apple-touch-icon" sizes="60x60" href="/images/apple-icon-60x60.png">
	<link rel="apple-touch-icon" sizes="72x72" href="/images/apple-icon-72x72.png">
	<link rel="apple-touch-icon" sizes="76x76" href="/images/apple-icon-76x76.png">
	<link rel="apple-touch-icon" sizes="114x114" href="/images/apple-icon-114x114.png">
	<link rel="apple-touch-icon" sizes="120x120" href="/images/apple-icon-120x120.png">
	<link rel="apple-touch-icon" sizes="144x144" href="/images/apple-icon-144x144.png">
	<link rel="apple-touch-icon" sizes="152x152" href="/images/apple-icon-152x152.png">
	<link rel="apple-touch-icon" sizes="180x180" href="/images/apple-icon-180x180.png">
	<link rel="icon" type="image/png" sizes="192x192"  href="/images/android-icon-192x192.png">
	<link rel="icon" type="image/png" sizes="32x32" href="/images/favicon-32x32.png">
	<link rel="icon" type="image/png" sizes="96x96" href="/images/favicon-96x96.png">
	<link rel="icon" type="image/png" sizes="16x16" href="/images/favicon-16x16.png">
	<link rel="manifest" href="/images/manifest.json">
	<meta name="msapplication-TileColor" content="#ffffff">
	<meta name="msapplication-TileImage" content="/ms-icon-144x144.png">
	<meta name="theme-color" content="#ffffff">
    <style>
        body {
            padding: 5px 5px 5px 5px;
        }
        
        html,
        body {
          height: 100%;
          /* The html and body elements cannot have any padding or margin. */
          font-size: 12px;
        }
        
        body {
	    background: url("data:image/png;base64, iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAMAAADz0U65AAAACVBMVEX4%2BPjs7Oz5%2Bfl%2BU6S3AAAAG0lEQVQImWNggAEmEGBkZEJjAAESAyiMxoACAAzwAGnXyNREAAAAAElFTkSuQmCC") repeat scroll 0 0 rgba(0, 0, 0, 0);
		}

        /* Wrapper for page content to push down footer */
        #wrap {
          min-height: 100%;
          height: auto;
          /* Negative indent footer by its height */
          margin: 0 auto -60px;
          /* Pad bottom by footer height */
          padding: 0 0 60px;
        }

        /* Set the fixed height of the footer here */
        #footer {
          height: 25px;
          background-color: #f5f5f5;
        }

        </style>
    <title>SMTProo Inbox</title>
   </head>
  <body>
  <div class="container-fluid" id="wrap">
		<table id="mailListing" class="table table-striped table-sm">
		  <thead>
			<tr>
			  <th scope="col">#</th>
			  <th scope="col">Action</th>
			  <th scope="col">To</th>
			  <th scope="col">From</th>
			  <th scope="col">Subject</th>
			  <th scope="col">Date</th>  
			</tr>
		  </thead>
		  <tbody>
			
		  </tbody>
		</table>
	</div>
    <script src="/js/jquery-3.1.1.min.js"></script>
    <script src="/js/bootstrap.min.js"></script>
	<script>
	Date.prototype.yyyymmdd = function() {
	  var mm = this.getMonth() + 1; // getMonth() is zero-based
	  var dd = this.getDate();

	  return [this.getFullYear(),
			  (mm>9 ? '' : '0') + mm,
			  (dd>9 ? '' : '0') + dd
			 ].join('');
	};
	$(function() {
		console.log( "ready!" );
		$.urlParam = function(name){
			var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
			if (results==null){
			   return null;
			}
			else{
			   return decodeURI(results[1]) || 0;
			}
		}
		var dir = $.urlParam('dir');
	
	  var mailAPI = "/inbox/get/mails?dir="+dir;
	  $.getJSON( mailAPI, {
		format: "json"
	  })
	  .done(function( data ) {
	        var counter = 0;
	  	$.each( data, function( i, item ) {
			//console.log(item)
			var newRowContent = "<tr>";
			  newRowContent += "<th scope=\"row\">"+(++counter)+"</th>"
			  newRowContent += "<td>"
			  newRowContent += "<a href=\"/inbox/get/mail?file="+item.file+"&dir="+dir+"\" target=\"_blank\">Download</a>"
			  newRowContent += "</td>";
			  newRowContent += "<td>"+$('<div/>').text(item.to).html()+"</td>";
			  newRowContent += "<td>"+$('<div/>').text(item.from).html()+"</td>";
			  newRowContent += "<td>"+$('<div/>').text(item.subject).html()+"</td>";
			  newRowContent += "<td>"+$('<div/>').text(item.date).html()+"</td>";
			  newRowContent += "</tr>";
			$("#mailListing tbody").append(newRowContent);
			
		  });
		  $("#mailListing tbody").append("<tr><td colspan=\"6\">Total Email: "+counter+"</td></tr>");
		  
		});
		
	    if(dir==null){
			var date = new Date();
	    	window.location.href="/inbox?dir="+date.yyyymmdd();
	    }
	});
		
	</script>
	<div id="footer">
         <div class="container text-center">
            <p class="text-muted credit">
                <p>Copyright © <span id="cp_year">20XX</span> <a href="https://github.com/homerfv/smtproo" target="_blank">SMTProo</a> · Open Source</p>
                <script>$("#cp_year").html(new Date().getFullYear())</script>
            </p>
          </div>
    </div>		
  </body>
</html>