(() => {
  const $ = (selector) => document.querySelector(selector);
  const themes = [
    {name:'Ocean Blue',accent:'#4f8fca',tint:'#e7f1fb',top:'#a9cff4',mid:'#c9e2fb',soft:'#e8f4ff'},
    {name:'Coral Red',accent:'#c96b62',tint:'#fae9e5',top:'#f2b8b1',mid:'#f8d8d3',soft:'#fdf0ed'},
    {name:'Lavender',accent:'#846ec2',tint:'#f0ebfb',top:'#c5b7ed',mid:'#e0d8f6',soft:'#f2effb'},
    {name:'Mint Green',accent:'#4d9873',tint:'#e8f5ed',top:'#a9d9bd',mid:'#cdebd8',soft:'#edf8f1'},
    {name:'Warm Amber',accent:'#ad7d38',tint:'#fbf1df',top:'#edcf9b',mid:'#f6e4c3',soft:'#fcf5e8'}
  ];
  let themeIndex=0, worldEnabled=true, toastTimer;
  const toast=(message)=>{const el=$('#toast');el.textContent=message;el.classList.add('show');clearTimeout(toastTimer);toastTimer=setTimeout(()=>el.classList.remove('show'),2400);};
  const applyTheme=()=>{const theme=themes[themeIndex];const root=document.documentElement;root.style.setProperty('--accent',theme.accent);root.style.setProperty('--tint',theme.tint);root.style.setProperty('--theme-top',theme.top);root.style.setProperty('--theme-mid',theme.mid);root.style.setProperty('--theme-soft',theme.soft);$('#themeButton').innerHTML='◐ <span>'+theme.name+'</span>';};
  $('#themeButton').addEventListener('click',()=>{themeIndex=(themeIndex+1)%themes.length;applyTheme();});
  const updateWorld=()=>{const button=$('#worldButton');button.classList.toggle('off',!worldEnabled);button.setAttribute('aria-pressed',String(worldEnabled));button.innerHTML='◎ World <b>'+(worldEnabled?'ON':'OFF')+'</b>';$('#worldStatus').textContent=worldEnabled?'Public websites are allowed':'Public websites are blocked';$('#worldStatusPill').textContent=worldEnabled?'ON':'OFF';};
  $('#worldButton').addEventListener('click',()=>{worldEnabled=!worldEnabled;updateWorld();toast(worldEnabled?'World browsing enabled':'World browsing disabled');});
  const openSearch=(raw)=>{const value=raw.trim();if(!value)return;if(!worldEnabled){toast('World is OFF. Public browsing is disabled in this preview.');return;}let url;if(/^https?:\/\//i.test(value))url=value;else if(value.includes('.')&&!value.includes(' '))url='https://'+value;else url='https://www.google.com/search?q='+encodeURIComponent(value);window.open(url,'_blank','noopener,noreferrer');};
  $('#searchForm').addEventListener('submit',(event)=>{event.preventDefault();openSearch($('#addressInput').value);});
  $('#backButton').addEventListener('click',()=>history.back());
  $('#forwardButton').addEventListener('click',()=>history.forward());
  $('#reloadButton').addEventListener('click',()=>location.reload());
  $('#homeButton').addEventListener('click',()=>showTab('home'));
  document.querySelectorAll('.quick-card').forEach(card=>card.addEventListener('click',()=>toast(card.dataset.message)));
  document.querySelectorAll('.topic-chip').forEach(chip=>chip.addEventListener('click',()=>{document.querySelectorAll('.topic-chip').forEach(item=>item.classList.remove('selected'));chip.classList.add('selected');if(chip.dataset.query!=='technology')openSearch(chip.dataset.query);}));
  function showTab(tab){const home=tab==='home';$('#homeContent').classList.toggle('hidden',!home);$('#exploreContent').classList.toggle('hidden',!home);$('#emptyState').classList.toggle('hidden',home);$('#pageTitle').textContent=home?'Welcome to CAENS':tab==='bookmarks'?'Your Bookmarks':'Your History';$('#pageSubtitle').textContent=home?'A calmer way to explore your digital world.':home?'':'Your browser activity will appear here.';$('#emptyTitle').textContent=tab==='bookmarks'?'No bookmarks yet':'No history yet';$('#emptyText').textContent=tab==='bookmarks'?'Websites you save will appear here.':'Websites you visit will appear here.';document.querySelectorAll('.nav-tab').forEach(button=>button.classList.toggle('active',button.dataset.tab===tab));}
  document.querySelectorAll('.nav-tab').forEach(button=>button.addEventListener('click',()=>showTab(button.dataset.tab)));
  $('#returnHome').addEventListener('click',()=>showTab('home'));
  updateWorld();applyTheme();
})();