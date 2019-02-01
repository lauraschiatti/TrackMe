import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {Data4helpComponent} from './data4help.component';

describe('Data4helpComponent', () => {
    let component: Data4helpComponent;
    let fixture: ComponentFixture<Data4helpComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [Data4helpComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(Data4helpComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
