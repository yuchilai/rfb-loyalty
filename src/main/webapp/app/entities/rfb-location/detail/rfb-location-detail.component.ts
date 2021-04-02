import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRfbLocation } from '../rfb-location.model';

@Component({
  selector: 'jhi-rfb-location-detail',
  templateUrl: './rfb-location-detail.component.html',
})
export class RfbLocationDetailComponent implements OnInit {
  rfbLocation: IRfbLocation | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rfbLocation }) => {
      this.rfbLocation = rfbLocation;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
