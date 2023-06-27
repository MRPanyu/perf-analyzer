var model = {
	fileKey: null,
	groupIdx: null,
	rootNodes: [],
	search: ''
}

$(function() {
	loadRecordFiles();
	layui.use(function(){
		var form = layui.form;
		form.render();
		
		// events
		form.on('select(fileSelect)', function(data) {
			onFileSelect(data.value);
		});
		form.on('select(groupSelect)', function(data) {
			onGroupSelect(data.value);
		});
		$('#search').change(onSearch);
		$('#search + .layui-input-affix .layui-icon-clear').click(onSearch);
		$('#btnExpandAll').click(function() { layui.treeTable.expandAll('treeTable', true); });
		$('#btnCollapseAll').click(function() { layui.treeTable.expandAll('treeTable', false); });
		$('#btnHelp').click(function() {
			var html = $("#helpContent").html();
			layui.layer.open({
				type: 1,
				area: ['500px', '300px'],
				content: html
			});
		});
		
		renderTreeTable();
	});
});

function loadRecordFiles() {
	$.ajax({
		url: 'getRecordFiles',
		type: 'post',
		dataType: 'json',
		success: function(map) {
			var fileSelect = $("#fileSelect").get(0);
			fileSelect.options.length = 1;
			for (var key in map) {
				var path = map[key];
				var option = new Option(path, key);
				fileSelect.options.add(option);
			}
			layui.form.render('select');
		}
	});
}

function onFileSelect(value) {
	model.fileKey = value;
	if (value === '') {
		var groupSelect = $("#groupSelect").get(0);
		groupSelect.options.length = 1;
		layui.form.render('select');
		onGroupSelect('');
	} else {
		$.ajax({
			url: 'getTimeGroups',
			type: 'post',
			data: { fileKey: model.fileKey },
			dataType: 'json',
			success: function(groups) {
				var groupSelect = $("#groupSelect").get(0);
				groupSelect.options.length = 1;
				for (var i = 0; i < groups.length; i++) {
					var g = groups[i];
					var option = new Option(g, '' + i);
					groupSelect.options.add(option);
				}
				layui.form.render('select');
				onGroupSelect('');
			}
		});
	}
}

function onGroupSelect(value) {
	model.groupIdx = value;
	if (value === '') {
		model.rootNodes = [];
		renderTreeTable();
	} else {
		$.ajax({
			url: 'getGroupData',
			type: 'post',
			data: { fileKey: model.fileKey, groupIdx: model.groupIdx },
			dataType: 'json',
			success: function(groupData) {
				if (groupData == null) {
					layui.layer.msg("Error");
				} else {
					model.rootNodes = groupData.rootNodes;
					renderTreeTable();
				}
			}
		});
	}
}

function onSearch() {
	var value = $("#search").val();
	model.search = value;
	renderTreeTable();
}

function filterData(nodes) {
	var search = model.search;
	var filtered = [];
	for (var i = 0; i < nodes.length; i++) {
		var node = nodes[i];
		if (search === '' || node.name.indexOf(search) >= 0) {
			filtered.push(fillNode(node));
		} else {
			var childrenFiltered = filterData(node.children);
			filtered = filtered.concat(childrenFiltered);
		}
	}
	return filtered;
}

function fillNode(node) {
	node.isParent = node.children != null && node.children.length > 0;
	return node;
}

function renderTreeTable() {
	layui.treeTable.render({
		elem: '#treeTable',
		id: 'treeTable',
		data: filterData(model.rootNodes),
		tree: {
			view: {
				showIcon: false
			}
		},
		cols: [[
			{field: 'name', title: '名称', width: 640, fixed: 'left'},
			// {field: 'type', title: '类型'},
			{field: 'executeCount', title: '执行次数', width: 120, sort: true},
			{field: 'successCount', title: '成功次数', width: 120, sort: true},
			{field: 'errorCount', title: '异常次数', width: 120, sort: true},
			{field: 'totalUseTime', title: '总耗时', width: 120, sort: true},
			{field: 'totalUseTimeExcludeChildren', title: '总耗时X', width: 120, sort: true},
			{field: 'avgUseTime', title: '平均耗时', width: 120, sort: true},
			{field: 'avgUseTimeExcludeChildren', title: '平均耗时X', width: 120, sort: true},
			{field: 'maxUseTime', title: '最大耗时', width: 120, sort: true},
			{field: 'maxUseTimeExcludeChildren', title: '最大耗时X', width: 120, sort: true},
			{field: 'successTotalUseTime', title: '成功总耗时', width: 120, sort: true},
			{field: 'successTotalUseTimeExcludeChildren', title: '成功总耗时X', width: 120, sort: true},
			{field: 'successAvgUseTime', title: '成功次均耗时', width: 120, sort: true},
			{field: 'successAvgUseTimeExcludeChildren', title: '成功次均耗时X', width: 120, sort: true},
			{field: 'successMaxUseTime', title: '成功最大耗时', width: 120, sort: true},
			{field: 'successMaxUseTimeExcludeChildren', title: '成功最大耗时X', width: 120, sort: true},
			{field: 'errorTotalUseTime', title: '异常总耗时', width: 120, sort: true},
			{field: 'errorTotalUseTimeExcludeChildren', title: '异常总耗时X', width: 120, sort: true},
			{field: 'errorAvgUseTime', title: '异常次均耗时', width: 120, sort: true},
			{field: 'errorAvgUseTimeExcludeChildren', title: '异常次均耗时X', width: 120, sort: true},
			{field: 'errorMaxUseTime', title: '异常最大耗时', width: 120, sort: true},
			{field: 'errorMaxUseTimeExcludeChildren', title: '异常最大耗时X', width: 120, sort: true}
		]],
		height: 'full-62'
	});
}
