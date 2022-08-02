import { TestBed } from '@angular/core/testing';

import { TestScoreService } from './test-score.service';

describe('TestScoreService', () => {
  let service: TestScoreService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TestScoreService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
