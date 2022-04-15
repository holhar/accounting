import { Component, OnInit } from '@angular/core';
import { Report } from '../report';
import { ReportsService } from '../reports.service';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {

  reports: Report[] | undefined;
  chartData: any[] = [];
  chartOptions: any;

  constructor(private reportsService: ReportsService) { }

  ngOnInit(): void {
    console.log('get data');
    this.reportsService.getReports().subscribe(data => {
      this.reports = data;
      console.log('reports initialized');
      this.setChartData();
      this.setChartOptions();
      console.log(this.chartOptions);
    });
  }

  setChartData() {
    console.log('set chart data');
    this.reports?.forEach(report => {
        this.chartData?.push({
          date: report.month + ' ' + report.year,
          expenditure: (report.expenditure === undefined ? 0 : report.expenditure) * -1
        })
    })
  }

  setChartOptions() {
    console.log('set options');
    this.chartOptions = {

      title: {
        text: 'Expenditure per month!!!',
      },
      legend: {
        data: ['Expenditure']
      },
      tooltip: {
      },
      xAxis: {
        data: this.chartData?.map(c => c.date)
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: 'Expenditure',
          type: 'line',
          data: this.chartData?.map(c => c.expenditure)
        }
      ]
    };
  }

}
