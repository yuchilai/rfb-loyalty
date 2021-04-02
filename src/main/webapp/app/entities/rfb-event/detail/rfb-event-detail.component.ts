import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRfbEvent } from '../rfb-event.model';

@Component({
  selector: 'jhi-rfb-event-detail',
  templateUrl: './rfb-event-detail.component.html',
})
export class RfbEventDetailComponent implements OnInit {
  rfbEvent: IRfbEvent | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rfbEvent }) => {
      this.rfbEvent = rfbEvent;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
