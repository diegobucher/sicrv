/*============== | Formulário |===========================*/

$(document).load(function() {
	$('.iconLogin').click(function(){$("#div1").slideDown("slow")});
  	$('#div1').mouseleave(function(){$("#div1").slideUp("fast")});  	
	$(".telefone").mask("(99)9999-9999");
	$(".cnpj").mask("999.999.999/9999-99");
	$(".cpf").mask("999.999.999-99");
	$(".cep").mask("99999-999");
	$(".data").mask("99/99/9999");
	$(".data").datepicker();
	$(".cgc").mask("9?999");
	$(".srVinculacao").mask("9999");
	$(".matricula").mask("a999999");
	$(".hora").mask("99:99:99");
	$(".cnpj5").mask("99999");
	$(".identificador").mask("aa.999999.999");
	$("#tabs").tabs();
	
	$("#dropIdentif").hide();
	$("#btnInfos").mouseover(function() {
		$("#dropIdentif").show();
	}).mouseout(function() {
		$("#dropIdentif").hide();
	});
	
	gridSetZebra();
});

$(document).ready(function(){
	$(".carregando").hide();
	$.unblockUI();	
});

function showStatus() {
	$("#idLoad").show();
	$.blockUI({message: null});
}
 
function hideStatus() {
	$("#idLoad").hide();		
	$.unblockUI();
}

/* ============== | Info |=============================== */

$(function() {
	$("#dropIdentif").hide();
	$("#btnInfos").mouseover(function() {
		$("#dropIdentif").show();
	}).mouseout(function() {
		$("#dropIdentif").hide();
	});
});

/* ============== | Tabela |=============================== */

function gridPesquisarLoad() {
	$('#gridPesquisar').dataTable({
		"bJQueryUI" : false,
		"sPaginationType" : "full_numbers",
		"asStripClasses" : [ "impar", "par" ],
		//"aoColumns" : [ null ],
		"oLanguage" : {
			"sProcessing" : "Processando...",
			"sLengthMenu" : "Exibir _MENU_ registros",
			"sZeroRecords" : "Nenhum registro correspondente encontrado",
			"sEmptyTable" : "Não há dados disponíveis na tabela",
			"sInfo" : "Exibindo _START_ até _END_ de _TOTAL_ registros",
			"sInfoEmpty" : "Exibindo 0 até 0 de 0 atividades",
			"sInfoFiltered" : "(Filtradas todas atividades - _MAX_ registros.)",
			"sInfoPostFix" : "",
			"sSearch" : "Pesquisar:",
			"sUrl" : "",
			"oPaginate" : {
				"sFirst" : "Primeiro",
				"sPrevious" : "Anterior",
				"sNext" : "Próximo",
				"sLast" : "Último"
			}
		}
	});
}

function gridSimplesLoad() {
	$('#gridPesquisar').dataTable({
		"bJQueryUI" : false,
		"asStripClasses" : [ "impar", "par" ],
		"bPaginate": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"oLanguage" : {
			"sProcessing" : "Processando...",
			"sLengthMenu" : "Exibir _MENU_ registros",
			"sZeroRecords" : "Nenhum registro correspondente encontrado",
			"sEmptyTable" : "Não há dados disponíveis na tabela",
			"sInfo" : "Exibindo _START_ até _END_ de _TOTAL_ registros",
			"sInfoEmpty" : "Exibindo 0 até 0 de 0 atividades",
			"sInfoFiltered" : "(Filtradas todas atividades - _MAX_ registros.)",
			"sInfoPostFix" : "",
			"sSearch" : "Pesquisar:",
			"sUrl" : "",
			"oPaginate" : {
				"sFirst" : "Primeiro",
				"sPrevious" : "Anterior",
				"sNext" : "Próximo",
				"sLast" : "Último"
			}
		}
	});
}

function gridSetZebra() {
	
	$('.zebra').dataTable({
		"bJQueryUI" : false,
		"asStripClasses" : [ "impar", "par" ],
		"bPaginate": false,
		"bFilter": false,
		"bSort": true,
		"bInfo": false
	});

	$('.zebra tbody tr:odd').addClass('par');			
	$('.zebra tbody tr:even').addClass('impar');	

	$('.zebra tbody tr').hover(function() {
		$(this).addClass('destacar');
	}, function() {
		$(this).removeClass('destacar');
	});
}

/* ============== | Modal |=============================== */

$(document).ready(function() {
	$(".btnFecharModal").click(function() {
		$(" .modalExcluir").dialog("close");
		$(".msg p").hide()
	});
});

/* ============== | Validação |=============================== */

function somenteNumero(e) {
	var tecla = e.charCode;
	if (tecla == undefined) { // Validação para o IE
		tecla = e.keyCode;
	}
	tecla = String.fromCharCode(tecla);
	if (e.which == 8 || e.which == 0 || /^\-?([0-9]+|Infinity)$/.test(tecla)) {
		return;
	} else {
		e.keyCode = 0;
		e.charCode = 0;
		e.returnValue = false;
		return false;
	}
}

function overSomenteNumeros(s) {
	var valor = $(s).val();
	if (valor != "" && valor != undefined
			&& !/^\-?([0-9]+|Infinity)$/.test(valor)) {
		$(s).val("");

	} else {
		return;
	}
}


/* ============== | Primefaces |=============================== */

/**  
 * Função Principal 
 * @param w - O elemento que será aplicado (normalmente this).
 * @param e - O evento para capturar a tecla e cancelar o backspace.
 * @param m - A máscara a ser aplicada.
 * @param r - Se a máscara deve ser aplicada da direita para a esquerda. Veja Exemplos.
 * @param a - 
 * @returns null  
 */
function maskIt(w,e,m,r,a){
    
    // Cancela se o evento for Backspace
    if (!e){
 	   e = window.event;
    }
    if (e.keyCode){
 	   code = e.keyCode;
    } else if (e.which) {
 	   code = e.which;
    }
    
    // Variáveis da função
    var txt  = (!r) ? w.value.replace(/[^\d]+/gi,'') : w.value.replace(/[^\d]+/gi,'').reverse();
    var mask = (!r) ? m : m.reverse();
    var pre  = (a ) ? a.pre : "";
    var pos  = (a ) ? a.pos : "";
    var ret  = "";

    if(code == 9 || code == 8 || txt.length == mask.replace(/[^#]+/g,'').length) return false;

    // Loop na máscara para aplicar os caracteres
    for(var x=0,y=0, z=mask.length;x<z && y<txt.length;){
            if(mask.charAt(x)!='#'){
                    ret += mask.charAt(x); x++;
            } else{
                    ret += txt.charAt(y); y++; x++;
            }
    }
    
    // Retorno da função
    ret = (!r) ? ret : ret.reverse();      
    w.value = pre+ret+pos;
}

PrimeFaces.locales['pt'] = {
	    closeText: 'Fechar',
	    prevText: 'Anterior',
	    nextText: 'Próximo',
	    currentText: 'Começo',
	    monthNames: ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'],
	    monthNamesShort: ['Jan','Fev','Mar','Abr','Mai','Jun', 'Jul','Ago','Set','Out','Nov','Dez'],
	    dayNames: ['Domingo','Segunda','Terça','Quarta','Quinta','Sexta','Sábado'],
	    dayNamesShort: ['Dom','Seg','Ter','Qua','Qui','Sex','Sáb'],
	    dayNamesMin: ['D','S','T','Q','Q','S','S'],
	    weekHeader: 'Semana',
	    firstDay: 1,
	    isRTL: false,
	    showMonthAfterYear: false,
	    yearSuffix: '',
	    timeOnlyTitle: 'Só Horas',
	    timeText: 'Tempo',
	    hourText: 'Hora',
	    minuteText: 'Minuto',
	    secondText: 'Segundo',
	    ampm: false,
	    month: 'Mês',
	    week: 'Semana',
	    day: 'Dia',
	    allDayText : 'Todo Dia'
	};


String.prototype.reverse = function(){
       return this.split('').reverse().join('');
};

function formatarDatas(){	
	$( "input[id*='Data']" ).each(function() {
		 valorData =  $( this ).val( ); 
		 if(valorData != null && valorData!= "" ){
			 valorData = valorData.replace("/","").replace("/","");
			 valorData = valorData.substring(0,2) +"/" +  valorData.substring(2,4) + "/" + valorData.substring(4,8);
			 $( this ).val(valorData);
		 }
	 });
}

function mascararData(){
   $("input[id*='Data']").mask('00/00/0000');
}

function mascararDataHora(){
	$("input[id*='DataHora']").mask('00/00/0000 00:00:00');
}

function limitarTextArea(){
	$('textarea[maxlength]').keyup(function(){
		//get the limit from maxlength attribute
		var limit = parseInt($(this).attr('maxlength'));
		//get the current text inside the textarea
		var text = $(this).val();
		//count the number of characters in the text
		var chars = text.length;

		//check if there are more characters then allowed
		if(chars > limit){
			//and if there are use substr to get the text before the limit
			var new_text = text.substr(0, limit);

			//and change the current text with the new text
			$(this).val(new_text);
		}
	});
}


function validaELimpa(elemento){
	if(!validarDateTime(elemento.value)){
		elemento.value = '';
	}
}

function validarDateTime(d) {
    //               dd        /      MM        / yyyy      h     :   mm  :   ss
    var re = /^([0-2]\d|3[01])\/(0[1-9]|1[0-2])\/\d{4} ([01]\d|2[0-3]):[0-5]\d:[0-5]\d$/;
//    var re = /^([0-2]\d|3[01])\/(0[1-9]|1[0-2])\/\d{4} (0\d|1[01]):[0-5]\d:[0-5]\d$/;
    return re.test(d);
}


function giveFocus(elementId){
	var element = document.getElementById(elementId);
	if(element != null){
		element.focus();
	}
}

//Método responsável por validar as horas
function verificaHora(){

   	$('.hora').mask('00:00',{
	   	clearIfNotMatch: true
	   	});

   	$('.hora' ).on( 'blur', function() {
   		valor = $( this ).val();
   		if(! /^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])?$/i.test(valor)){
   			 $( this ).val('');
	    }
   });
}


function verificaMatricula(){
	$('.matricula').mask('X000000', {translation: {
		  'X':{pattern:/[CcPp]/} 
	  	 } 
	});
	
	$('.matricula' ).on('blur', function() {
   		valor = $( this ).val();
   		if(! /[CcPp][0-9]{6,}/i.test(valor)){
  			 $( this ).val('');
	    }
   });
}

/*---------------ACORDION------------------------------------------------------*/

$(document).ready(function() {
//	var mais = '<a href="#"><img src="/resources/imagens/mais.gif)"  class="maismenos" /></a>'
	/*$('table.gridColEstatistico tbody tr:not(.gridSub):even').addClass('impar');*/			
	$('table.gridColEstatistico tbody tr:not(.gridSub)').hide();
	$('.gridSub td').css( {background:'#ebebeb', borderBottom: '1px dotted #666', borderTop: '1px dotted #666'} );	 
//	$('.gridSub th').css({background:'#ebebeb', borderBottom: '1px dotted #666', borderTop: '1px dotted #666'}).prepend(mais);
	
	var imgMais = window.location.href.split('/pages/')[0]+'/resources/imagens/mais.gif';
	var imgMenos = window.location.href.split('/pages/')[0] +'/resources/imagens/menos.gif';
	
	$('img', $('.gridSub th'))
	.click(function(event){
		event.preventDefault();
		if (($(this).attr('src')) == imgMenos){
			$(this).attr('src', imgMais)
			.parents()
			.siblings('tr').hide(); 
		} else {
			$(this).parents('tbody').siblings('tbody')
			.children('tr:not(tr.gridSub)').hide()
			$('.maismenos').attr('src', imgMais)
			$(this).attr('src', imgMenos)
			.parents().siblings('tr').show();
		}; 
	});
	$('.gridColEstatistico tbody tr:odd').addClass('um');
	$('.gridColEstatistico tbody tr:even').addClass('dois');
});


function ajuda(url) {
	window.open(
			url,
			'_blank',
			'width=800',
			'height=600',
			'resizable=yes',
			'scrolling=auto',
			'top=0',
			'left=0'
	)
	return false;
}

