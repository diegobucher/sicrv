// JavaScript Document

window.myLineDiary;
window.myLineMonthy;
var config = "";
var controle = "";

window.chartColors = {
	red: 'rgb(255, 99, 132)',
	orange: 'rgb(255, 159, 64)',
	yellow: 'rgb(255, 205, 86)',
	green: 'rgb(75, 192, 192)',
	blue: 'rgb(54, 162, 235)',
	purple: 'rgb(153, 102, 255)',
	grey: 'rgb(231,233,237)'
};


function graficoMes() {
		controle = document.getElementById("controle");
		controle.innerHTML = "";
		controle.innerHTML = "<canvas id=\"canvas\" ></canvas>";

        var MONTHS = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
        config = {
            type: 'line',
            data: {
                labels: ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho","Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"],
                datasets: [{
                    label: "Tempo de indisponibilidade",
                    backgroundColor: window.chartColors.red,
                    borderColor: window.chartColors.red,
                    data: [100, 34, 58, 33, 90,74, 48, 35, 58, 46, 21, 35],
                    fill: false,
					pointRadius: 4,
					lineTension: 0,
                }]
            },
 				options: {
                responsive: true,
                tooltips: {
                    mode: 'index',
                    intersect: false,
                },
                hover: {
                    mode: 'nearest',
                    intersect: true
                },
                scales: {
                    xAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Mês'
                        }
                    }],
                    yAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Tempo (Minutos)'
                        }
                    }]
                }
            }
        };

		var ctx = document.getElementById("canvas").getContext("2d");
		window.myLineMonthy = new Chart(ctx, config);

        document.getElementById('randomizeDataMes').addEventListener('click', function() {
			graficoDia();
        });
		
}

function graficoDia() {
		controle = document.getElementById("controle");
		controle.innerHTML = "";
		controle.innerHTML = "<canvas id=\"canvas\" ></canvas>";
        var MONTHS = ["1", "2", "3", "4", "5", "6", "7","8", "9", "10", "11", "12", "13", "14","15", "16", "17", "18", "19", "20", "21","22", "23", "24", "25", "26", "27", "28","29", "30","31"];
        config = {
            type: 'line',
            data: {
                labels: ["1", "2", "3", "4", "5", "6", "7","8", "9", "10", "11", "12", "13", "14","15", "16", "17", "18", "19", "20", "21","22", "23", "24", "25", "26", "27", "28","29", "30","31" ],
                datasets: [{
                    label: "Tempo de indisponibilidade",
                    backgroundColor: window.chartColors.red,
                    borderColor: window.chartColors.red,
                    data: [12,35,85,32,64,66,89,25,25,16,37,25,92,85,76,45,19,11,75,25,25,24,32,59,84,60,60,24,79,67,28],
                    fill: false,
					pointRadius: 4,
					lineTension: 0,
                }]
            },
 				options: {
                responsive: true,
			
                tooltips: {
                    mode: 'index',
                    intersect: false,
                },
                hover: {
                    mode: 'nearest',
                    intersect: true
                },
                scales: {
                    xAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Último 30 dias'
                        }
                    }],
                    yAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Tempo (Minutos)'
                        }
                    }]
                }
            }
        };

		var ctx = document.getElementById("canvas").getContext("2d");
		window.myLineDiary = new Chart(ctx, config);

        document.getElementById('randomizeDataDia').addEventListener('click', function() {
           		graficoMes();
            });

}

/*
var canvas = new Chart(ctx, {
    type: 'line',
    data: data,
    options: options
});

var canvas = {
    labels: ["January", "February", "March", "April", "May", "June", "July"],
    datasets: [
        {
            label: "My First dataset",
            fill: false,
            lineTension: 0.1,
            backgroundColor: "rgba(75,192,192,0.4)",
            borderColor: "rgba(75,192,192,1)",
            borderCapStyle: 'butt',
            borderDash: [],
            borderDashOffset: 0.0,
            borderJoinStyle: 'miter',
            pointBorderColor: "rgba(75,192,192,1)",
            pointBackgroundColor: "#fff",
            pointBorderWidth: 1,
            pointHoverRadius: 5,
            pointHoverBackgroundColor: "rgba(75,192,192,1)",
            pointHoverBorderColor: "rgba(220,220,220,1)",
            pointHoverBorderWidth: 2,
            pointRadius: 1,
            pointHitRadius: 10,
            data: [65, 59, 80, 81, 56, 55, 40],
            spanGaps: false,
        }
    ]
};*/