<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link href="{{ public_path('/css/stock.css') }}" rel="stylesheet" type="text/css">
    <title>Info product</title>
</head>

<body>
    <div class="logo">
        <div class="item"><img src="{{ public_path('/imagenes/ebrigge_logo.jpg') }}"></div>
        <div class="item2">
            <h2>Info product</h2>
            <h3>{{ $day }}</h3>
        </div>
    </div>
    <div class="data" style="margin-top: 30px;">
        <table id="customers">
            <thead>
                <tr>
                    <th rowspan="1">Code</th>
                    <th rowspan="1">Name</th>
                    <th rowspan="1">Description</th>
                    <th colspan="1">Quantity</th>
                    <th rowspan="1">Price</th>
                </tr>
            </thead>

            <tbody>
                @foreach ($data as $product)
                    <tr>
                        <td style="text-align: center;">{{ $product->id }}</td>
                        <td>{{ $product->name }}</td>
                        <td>{{ $product->description }}</td>
                        <td style="text-align: center;">{{ $product->quantity }}</td>
                        <td style="text-align: center;">${{ $product->price }}</td>
                    </tr>
                @endforeach
            </tbody>
        </table>
    </div>
</body>

</html>
