import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRfbUser } from '../rfb-user.model';

@Component({
  selector: 'jhi-rfb-user-detail',
  templateUrl: './rfb-user-detail.component.html',
})
export class RfbUserDetailComponent implements OnInit {
  rfbUser: IRfbUser | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rfbUser }) => {
      this.rfbUser = rfbUser;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
