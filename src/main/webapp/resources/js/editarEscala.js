$(document).ready(function() {
				
	var idEquipe = $('#equipe').val();
	var dataInicial = $('#dataInicio').val();
	var dataFim = $('#dataFim').val();
	
	showStatus();
	
	$('#divCalendar').fullCalendar({
		events : {	
			url: '/sicrv/ConsultarGeracaoEscala',
			type: 'POST',
			data: {
				equipe: idEquipe,
				dataInicial: dataInicial,
				dataFim: dataFim
			},
			error: function() {
				alert('Ocorreu um erro ao obter os eventos!');
			},
			complete: function () {
				hideStatus();
		    }
		},
		defaultDate: dataInicial,
		firstDay: 1,
		header: {
			left: 'prev,next',
			center: 'title',
			right: 'month,agendaDay'
		},
		navLinks: true, // can click day/week names to navigate views
		editable: true, // Habilitar mover o evento
		eventDurationEditable: false, //Bloquear a alteração da duração
//		slotDuration: '01:00:00', //Deixar disponivel slots de 1 hora
		allDaySlot: false, //Não Exibir menu superior de dia inteiro
		viewRender: function(currentView){ //Função para bloquear a navegação fora do periodo
			
			var minDate =$.fullCalendar.moment(dataInicial),
			maxDate = $.fullCalendar.moment(dataFim)
			
			minDate.day(1);
			maxDate.day(7);
			
			// Past
			if (minDate >= currentView.start && minDate <= currentView.end) {
				$(".fc-prev-button").prop('disabled', true); 
				$(".fc-prev-button").addClass('fc-state-disabled'); 
			}
			else {
				$(".fc-prev-button").removeClass('fc-state-disabled'); 
				$(".fc-prev-button").prop('disabled', false); 
			}
			
			// Future
			if (maxDate >= currentView.start && maxDate < currentView.end) {
				$(".fc-next-button").prop('disabled', true); 
				$(".fc-next-button").addClass('fc-state-disabled'); 
			} else {
				$(".fc-next-button").removeClass('fc-state-disabled'); 
				$(".fc-next-button").prop('disabled', false); 
			}
		},
		eventDrop: function (event, delta, revertFunc){
			if(!confirm("Deseja confirmar esta mudança?")){
				revertFunc();
			} else {
				showStatus();
				$.ajax({
					url: '/sicrv/AtualizarDiaEmpregadoEscala',
					type: 'POST',
					dataType: 'JSON',
					data: ({
						eventoJson : JSON.stringify(event),
						dataInicial: dataInicial,
						dataFim: dataFim
					}),
					success: function(data){
						if(data.erro == 'true'){
							revertFunc();
						}
						alert(data.mensagem);
					},
					error: function(){
						alert('Erro! Não foi possivel efetuar a mudança.');
						revertFunc();
					},
					complete: function(){
						hideStatus();
					}
				});
			}
		},
		eventClick: function (event){
			var empregado = event.title;

			//Se clicar no mesmo então limpa tudo.
			if($('div.fc-month-view a.fc-event').has("span:contains('"+empregado+"')").hasClass('empregadoSelecionado')){
				$('div.fc-month-view a.fc-event').has("span").removeClass('empregadoSelecionado');
				$('div.fc-month-view a.fc-event').has("span").removeClass('empregadoNaoSelecionado');
				
			}else {
				
				$('div.fc-month-view a.fc-event').has("span:contains('"+empregado+"')").addClass('empregadoSelecionado');
				$('div.fc-month-view a.fc-event').has("span:contains('"+empregado+"')").removeClass('empregadoNaoSelecionado');
				
				
				$('div.fc-month-view a.fc-event').not(":has(span:contains('"+empregado+"'))").addClass('empregadoNaoSelecionado');
				$('div.fc-month-view a.fc-event').not(":has(span:contains('"+empregado+"'))").removeClass('empregadoSelecionado');
			}
		}
	});
	
});

function pesquisarEmpregados(empregado){
	
	empregado = empregado.toUpperCase();
	
	$('div.fc-month-view a.fc-event').each(function(){
		
		var nomeEach = $(this).find('span').text().toUpperCase();
		if(nomeEach.indexOf(empregado) !== -1){
			 $(this).addClass('empregadoSelecionado');
			 $(this).removeClass('empregadoNaoSelecionado');
		} else {
			 $(this).addClass('empregadoNaoSelecionado');
			 $(this).removeClass('empregadoSelecionado');
		}
		
		
	});
}