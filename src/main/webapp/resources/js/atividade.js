/* Formatting function for row details - modify as you need */
function format ( d ) {
	
	var subtable ='';

	if(d.listaSubAtividade.length > 0 ){
		subtable += '<table id="subtable" cellpadding="5" cellspacing="0" border="0" style="width:100%">';
		
		/**/
		subtable += '	<thead>';
		subtable += '        <tr>';
//		subtable += '            <th style="width:10%"></th>';
		subtable += '            <th style="width:40%" class="borderRight">SUBATIVIDADE</th>';
		subtable += '            <th style="width:45%" class="borderRight">PRIORIDADE</th>';
		subtable += '            <th style="width:15%"></th>';
		subtable += '        </tr>';
		subtable += '    </thead>';
		
		for (index = 0; index < d.listaSubAtividade.length; ++index) {
			classe = index % 2 == 0? 'dois' : 'um';
			subtable+='<tr class="'+classe+'">'
			
//			subtable+='<td>'
//			subtable+='</td>'
				
			subtable+='<td class="borderRight" style="padding-left: 10px !important">'
				subtable+=d.listaSubAtividade[index].nome;
			subtable+='</td>'
				
			subtable+='<td class="centralizar borderRight">'
					subtable+=d.listaSubAtividade[index].prioridade;
			subtable+='</td>'
				
			var id = d.listaSubAtividade[index].id;
			
			subtable+='<td class="centralizar">'
				subtable+= '<i class="fa fa-edit marginRight iconCaixa" title="Clique aqui para alterar um registro" onclick="editarAtividade(\''+id+'\')"></i>' +     
				'<i class="fa fa-remove marginRight iconCaixa" title="Clique aqui para excluir um registro" onclick="excluirAtividade(\''+id+'\')"></i>' + 
				'<i class="fa fa-info-circle marginRight iconCaixa" title="Clique aqui para visualizar um registro" onclick="detalharAtividade(\''+id+'\')"></i>';
			subtable+='</td>'
				
				subtable+='</tr>'
		}
		
		subtable+='</table>'
	}
	return subtable;
}

function pesquisar(){
	
	$('#example').show();

	if(!$.fn.DataTable.isDataTable( '#example' ) ){
		
		var table = $('#example').DataTable( {
			"pagingType": "full_numbers",
			"bFilter": false,
//			"lengthMenu" : [5,10,15],
			"dom": "t<'rodape'ipl>",
			"bLengthChange": false,
			"language": {"url": "../../resources/json/Portuguese-Brasil.json"},
		    "ajax": {
		    	"url"  : "../../ConsultarAtividade",
		    	"type" : "POST",
		    	"data" : function (d) { 
		    		d.nomeAtividade = $('#formConsulta\\:idNome').val();
		    		d.idUnidade = $('#formConsulta\\:idUnidade').val();
		    	}
		    }, 
		    "columns": [
		            {
		                "orderable": false,
		                "sortable":  false,
                        "class" : "details-control",
		                "render":  function (data, type, row, meta){
	                		var span = "";
		                
	                		if(row.temFilhos != undefined && row.temFilhos == true){
		                		span = "<span class='expandir'></span>";
		                	}
		                	
		                	return span;
		                }
		            },
		            { "data": "nome",
            	      "class" : "borderRight"
    	    	    },
		            { "data": "periodicidade",
              	      "class" : "borderRight paddingLeft"
		            },
		            { "data": null ,
					  "bSortable": false,
					  "defaultContent": "<i class='fa fa-edit marginRight iconCaixa' title='Clique aqui para alterar um registro'></i>" + 
					  "<i class='fa fa-remove marginRight iconCaixa' title='Clique aqui para excluir um registro'></i>" +
					  "<i class='fa fa-info-circle marginRight iconCaixa' title='Clique aqui para visualizar um registro'></i>",
					  "className": "centralizar" 
					  },
					 
		     		 { "data": "id" ,
					   "visible": false
					}
					  
					 
		        ],
	        "drawCallback" : function (setting){
		    	ajustarExpansaoGrid();
		        },
		    "order": [[1, 'asc']],
		    "initComplete" : function (settings, json){
			    	ajustarExpansaoGrid();
			    }
		    
		    } );
		
//		table.ajax.reload(function (json){
//			$('.details-control:not(:contains(true))').removeClass();
//		});
		     
	    // Add event listener for opening and closing details
	    $('#example tbody').on('click', 'td.details-control', function () {
	        var tr = $(this).closest('tr');
	        var row = table.row( tr );
	 
	        if ( row.child.isShown() ) {
	            // This row is already open - close it
	            row.child.hide();
	            tr.removeClass('shown');
	        }
	        else {
	            // Open this row
	            row.child( format(row.data()) ).show();
	            tr.addClass('shown');
	        }
	    } );
	    
		//Associa o bind do click na table Externa
		  $('#example').on( 'click', 'tbody > :not(#subtable > tbody > tr) > td >i.fa-edit', function () {
		        var data = table.row( $(this).parents('tr') ).data();
		        editarAtividade(data.id);
			} );
		  
		  $('#example').on( 'click', 'tbody > :not(#subtable > tbody > tr) > td >i.fa-remove', function () {
		        var data = table.row( $(this).parents('tr') ).data();
		        excluirAtividade(data.id);
			} );
		  
		  $('#example').on( 'click', 'tbody > :not(#subtable > tbody > tr) > td > i.fa-info-circle', function () {
			  var data = table.row( $(this).parents('tr') ).data();
			  detalharAtividade(data.id);
		  } );
	} else {
		oTable = $("#example").dataTable();
		oTable._fnAjaxUpdate()
//    	ajustarExpansaoGrid();
	}
	
	
	$('#formConsulta\\:pesquisado').val('true');
}
 
$(document).ready(function() {
//	if($('#formConsulta\\:idNome').val() != ''){
	if($('#formConsulta\\:pesquisado').val() == "true"){
		pesquisar();
	}
} );

function ajustarExpansaoGrid(){
	$('td.details-control:not(:has(span))').removeClass();
}

