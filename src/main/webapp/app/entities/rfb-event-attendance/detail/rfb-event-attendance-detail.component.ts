import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRfbEventAttendance } from '../rfb-event-attendance.model';

@Component({
  selector: 'jhi-rfb-event-attendance-detail',
  templateUrl: './rfb-event-attendance-detail.component.html',
})
export class RfbEventAttendanceDetailComponent implements OnInit {
  rfbEventAttendance: IRfbEventAttendance | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rfbEventAttendance }) => {
      this.rfbEventAttendance = rfbEventAttendance;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
