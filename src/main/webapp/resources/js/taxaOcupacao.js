function desenharGrafico (dados){
		     
	var ctxLine = document.getElementById("chartOcupacao").getContext("2d");
	
	window.myLine = new Chart(ctxLine, {
		type: 'horizontalBar',
		data: dados,
		options: {
			responsive: true,
			legend: {
				display: true
			},
			title:{
				display:false,
				text:'Taxa de Ocupação',
				fontSize:20
			},
			tooltips: {
				mode: 'index',
				intersect: true,
				titleMarginBottom:10,
			},
			hover: {
				mode: 'nearest',
				intersect: true
				, onHover: function(e, el) {
				      $("#chartOcupacao").css("cursor", el[0] ? "pointer" : "default");
			    }
			},
			scales: {
				xAxes: [{
					display: true,
					scaleLabel: {
						display: false,
						labelString: 'Percentual'
					},
					ticks:{
						beginAtZero: true,
						max: 100
					}
				}],
				yAxes: [{
					display: true,
					scaleLabel: {
						display: true,
						labelString: 'Atividades'
					}
				}]
			}
		}
	});
	window.myLine.update();
	
	var canvas = document.getElementById("chartOcupacao");
	canvas.onclick = function(evt) {
		   var activePoint = window.myLine.getElementAtEvent(evt)[0];
		   
		   if(activePoint != null){
			   var label = activePoint._model.label;
			   detalharAtividade([{name:'nomeAtividade', value:label}]);
		   }
	};
}

function pesquisar(){
	
    window.chartColors = {
		red:'rgb(255, 0, 0)',
		blue:'rgb(54, 162, 235)',
		orange:'rgb(255, 153, 51)',
	};
    
    var dataInicio = $('#dataInicio\\:item_input').val();
    var dataFim = $('#dataFim\\:item_input').val();
    var idEquipe = $('#idEquipe\\:combo').val()
    
	$.ajax({
		datatype: 'json', 
		url: ctx + 'ConsultaTaxaOcupacaoGrafico',
		data: {
			dataInicio: dataInicio,
			dataFim: dataFim,
			idEquipe: idEquipe
		},
		async: false,
	   success: function(data){
	     // Handle the complete event
			   var dados = {
					   labels: data.nomeAtividadesList,
					   datasets: [{
						   label: "Ocupação",
						   backgroundColor: window.chartColors.blue,
						   borderColor: window.chartColors.blue,
						   data: data.ocupacaoDataSet,
						   pointRadius: 5,
						   fill: false
					   },{
						   
						   label: "Curva Padrão",
						   backgroundColor: window.chartColors.orange,
						   borderColor: window.chartColors.orange,
						   data: data.curvaPadraoDataSet,
						   pointRadius: 5,
						   fill: false
					   }],
			   };
			   
			   desenharGrafico(dados);
	   }
	});
	
  }

function criarDataTable() {
		
	$('.zebra').dataTable({
		"bJQueryUI" : false,
		"sPaginationType": "full_numbers",
		"asStripClasses" : [ "impar", "par" ],
		"columnDefs" : [ 
		    			{className : "center", orderable : true, "targets": [0]}
		    			,{className : "center borderLeft", orderable : false, "targets": [1]}
		    			,{className : "center borderLeft", orderable : false,  "targets": [2]}
		    			,{className : "center borderLeft", orderable : false,  "targets": [3]}
		    			,{className : "center borderLeft", orderable : false,  "targets": [4]}
		    			,{className : "center borderLeft", orderable : false,  "targets": [5]}
		    			,{className : "center borderLeft", orderable : false,  "targets": [6]}
		    			,{className : "center borderLeft", orderable : false,  "targets": [7]}
		    		]
		,"oLanguage":{
			"sProcessing": "Processando...",
			"sLengthMenu": "Exibir _MENU_ registros",
			"sZeroRecords": "Nenhum registro correspondente encontrado",
			"sEmptyTable": "Não há dados disponíveis na tabela",
			"sInfo": "Exibindo _START_ até _END_ de _TOTAL_ registros",
			"sInfoEmpty": "Exibindo 0 até 0 de 0 atividades",
			"sInfoFiltered": "(Filtradas todas atividades - _MAX_ registros.)",
			"sInfoPostFix": "",
			"sSearch": "Pesquisar:",
			"sUrl": "",
			"oPaginate": {
				"sFirst":    "Primeiro",
				"sPrevious": "Anterior",
				"sNext":     "Próximo",
				"sLast":     "Último"
			}
		}
		, "createdRow": function (row, data, index){
	
			$.each(data, function( indexLinha, value ) {
				if(value.indexOf("-") != -1 ){
					var pos = value.indexOf("-") ;
	
					var curva = value.substring(0,pos).trim();
					var ocupacao = value.substring(pos+1).trim();
					ocupacao = Number(ocupacao);
					curva = Number(curva);
	
					if(isInteger(ocupacao) && isInteger(curva) ){
						
						if(curva > ocupacao){
							$("td", row).eq(indexLinha).addClass("bgRed");
						}
						
					}
				}
			});
				
		}
	});
}

function isInteger(num) {
  return (num ^ 0) === num;
}
