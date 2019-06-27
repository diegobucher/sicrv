function acoesAposPesquisar() {

	$("#tableBancoFolga_data tr[id*='_row_']").each(function() {
		var rowIndex = $(this).attr('id').split('_row_')[1];
		
		if(rowIndex == 0){
			$(this).addClass('columnHeaderSubtable');
		} else if ($(this).text().indexOf('TOTAL:') > -1){
			$(this).addClass('columnHeaderSubtable');
		} else {
			if(rowIndex % 2 == 0){
				$(this).addClass('par');
			} else {
				$(this).addClass('impar');
			}
		}
	});
}
