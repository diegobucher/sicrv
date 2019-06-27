function showModal(){
	$("#id_modal").css("display", "block");
}

function hideModal(){
	$("#id_modal").css("display", "none");
}

function carregarDataTables(){
	
	var tabela2 = $("#table_empregados_escalados").DataTable({
		"columnDefs" : [ 
			{className :"dt-head-left", "targets": [0]}
			,{className :"dt-center", "targets": [1]}
		],
		"scrollY" : "300px",
		"scrollCollapse" : true,
		"info" : false,
		"searching" : false,
		"paging" : false,
		"language" : {
			"emptyTable" : "Nenhum registro encontrado",
			"info" : "Mostrando de _START_ até _END_ de _TOTAL_ registros",
			"infoEmpty" : "Mostrando 0 até 0 de 0 registros",
			"infoFiltered" : "(Filtrados de _MAX_ registros)",
			"infoPostFix" : "",
			"infoThousands" : ".",
			"lengthMenu" : "_MENU_ resultados por página",
			"loadingRecords" : "Carregando...",
			"processing" : "Processando...",
			"zeroRecords" : "Nenhum registro encontrado",
			"search" : "Pesquisar",
			"paginate" : {
				"next" : "Próximo",
				"previous" : "Anterior",
				"first" : "Primeiro",
				"last" : "Último"
			},
			"aria" : {
				"sortAscending" : ": Ordenar colunas de forma ascendente",
				"sortDescending" : ": Ordenar colunas de forma descendente"
			}
		}
	});
	
	showModal();
//	
//	tabela1.columns.adjust().draw();
	tabela2.columns.adjust().draw();
	
}

function acoesPosPesquisar() {
	
	$("table.escala").children().find("td.diaUtilTrabalhado, td.colorEscala").click( function (){
		var data = $(this).find('.data').text().trim();
		detalharDia([{name:'dia', value:data}]);
	})
}
