import { AppPage } from './app.po';

describe('workspace-project Data4HelpApp', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  it('should display welcome message', () => {
    page.navigateTo();
    expect(page.getTitleText()).toEqual('Welcome to data4help!');
  });
});
