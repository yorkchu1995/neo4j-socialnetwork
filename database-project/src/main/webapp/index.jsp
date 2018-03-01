<!doctype html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>NEO4J-QUERY</title>

    <link rel="stylesheet" type="text/css" href="css/styles.css">

</head>
<body>
<div class="jq22-container" style="padding-top:10px">
    <div class="login-wrap">
        <div class="login-html">
            <input id="tab-1" type="radio" name="tab" class="sign-in" checked=""><label for="tab-1" class="tab">First Query</label>
            <input id="tab-2" type="radio" name="tab" class="sign-up"><label for="tab-2" class="tab">Second Query</label>
            <div class="login-form">
                <div class="sign-in-htm">
                    <form action="/FirstQuery" method="get" id="form">
                        <div class="group">
                            <label for="user" class="label">Person-ids</label>
                            <input id="user" name="personId" type="text"  class="input">
                        </div>
                        <div class="group">
                            <input id="check" type="checkbox" class="check" checked="">
                        </div>
                        <div class="group">
                            <input type="submit" class="button" value="Query">
                        </div>
                        <div class="hr"></div>
                    </form>
                </div>

                <div class="sign-up-htm">
                    <form action="/SecondQuery" method="get" id="form2">
                        <div class="group">
                            <label for="user" class="label">startTime</label>
                            <input name="startTime" type="text" class="input">
                        </div>
                        <div class="group">
                            <label for="pass" class="label">endTime</label>
                            <input name="endTime" id="pass" class="input">
                        </div>
                        <div class="group">
                            <label for="pass" class="label">locationId</label>
                            <input name="locationId" class="input">
                        </div>
                        <div class="group">
                            <input type="submit" class="button" value="Query">
                        </div>
                        <div class="hr"></div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<div style="text-align:center;margin:50px 0; font:normal 14px/24px 'MicroSoft YaHei';">
</div>
<script src="http://www.w2bc.com/scripts/2bc/_gg_980_90.js" type="text/javascript"></script></body>
</html>