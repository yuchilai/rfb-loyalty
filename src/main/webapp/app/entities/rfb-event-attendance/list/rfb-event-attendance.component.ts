import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IRfbEventAttendance } from '../rfb-event-attendance.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { RfbEventAttendanceService } from '../service/rfb-event-attendance.service';
import { RfbEventAttendanceDeleteDialogComponent } from '../delete/rfb-event-attendance-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-rfb-event-attendance',
  templateUrl: './rfb-event-attendance.component.html',
})
export class RfbEventAttendanceComponent implements OnInit {
  rfbEventAttendances: IRfbEventAttendance[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected rfbEventAttendanceService: RfbEventAttendanceService,
    protected modalService: NgbModal,
    protected parseLinks: ParseLinks
  ) {
    this.rfbEventAttendances = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.isLoading = true;

    this.rfbEventAttendanceService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IRfbEventAttendance[]>) => {
          this.isLoading = false;
          this.paginateRfbEventAttendances(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.rfbEventAttendances = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IRfbEventAttendance): number {
    return item.id!;
  }

  delete(rfbEventAttendance: IRfbEventAttendance): void {
    const modalRef = this.modalService.open(RfbEventAttendanceDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.rfbEventAttendance = rfbEventAttendance;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateRfbEventAttendances(data: IRfbEventAttendance[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.rfbEventAttendances.push(d);
      }
    }
  }
}
