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

  constructor(private reportsService: ReportsService) { }

  ngOnInit(): void {
    this.reportsService.getReports().subscribe(data => {
      this.reports = data;
    })
  }

}
