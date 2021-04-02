import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IRfbUser } from '../rfb-user.model';
import { RfbUserService } from '../service/rfb-user.service';
import { RfbUserDeleteDialogComponent } from '../delete/rfb-user-delete-dialog.component';

@Component({
  selector: 'jhi-rfb-user',
  templateUrl: './rfb-user.component.html',
})
export class RfbUserComponent implements OnInit {
  rfbUsers?: IRfbUser[];
  isLoading = false;

  constructor(protected rfbUserService: RfbUserService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.rfbUserService.query().subscribe(
      (res: HttpResponse<IRfbUser[]>) => {
        this.isLoading = false;
        this.rfbUsers = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IRfbUser): number {
    return item.id!;
  }

  delete(rfbUser: IRfbUser): void {
    const modalRef = this.modalService.open(RfbUserDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.rfbUser = rfbUser;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
