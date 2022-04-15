import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { Report } from './report';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {

  constructor(private httpClient: HttpClient) { }

  getReports(): Observable<Report[]> {
    return this.httpClient.get<Report[]>('http://localhost:8080/reports');
  }
}
